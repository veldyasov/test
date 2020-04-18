

//import io.reactivex.*;
//import io.reactivex.Observable;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Action;
//import io.reactivex.functions.BiFunction;
//import io.reactivex.functions.Consumer;
//import io.reactivex.functions.Function;
//import io.reactivex.observers.DisposableObserver;
//import io.reactivex.schedulers.Schedulers;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entities.Conference;
import entities.Person;

import javax.swing.text.DateFormatter;


public class App {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static Date getNextDayOfDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public static void main(String[] args) throws IOException {

        // read json from file
        Gson gson = new Gson();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        List<Person> persons = gson.fromJson(new FileReader(classloader.getResource("input_data.json").getFile()), new TypeToken<ArrayList<Person>>() {}.getType());

        // удаляем повторяющиеся даты, сортируем доступные даты, оставляем те даты котрые идут подряд
        persons = persons.stream()
                .map(p -> {
                    Collections.sort(p.getAvailableDates());
                    List<Date> availableDates = p.getAvailableDates().stream()
                            .distinct()
                            .filter(date -> Collections.binarySearch(p.getAvailableDates(), getNextDayOfDate(date), Date::compareTo) >= 0)
                            .sorted()
                            .collect(Collectors.toList());
                    return new Person(p.getFirstName(), p.getLastName(), p.getCountry(), p.getEmail(), availableDates);
                })
                .filter(person -> person.getAvailableDates() != null && person.getAvailableDates().size() > 0)
                .collect(Collectors.toList());

        // группируем людей по странам
        Map<String, List<Person>> personsByCountry = persons.stream()
                .collect(Collectors.groupingBy(new Function<Person, String>() {
                    @Override
                    public String apply(Person person) {
                        return person.getCountry();
                    }
                }));

        // выводим мапку где, ключ - страна, значение - мапка где, ключ - дата, значение - список людей
        List<Conference> conferences = personsByCountry.entrySet().stream()
                .map(new Function<Map.Entry<String, List<Person>>, Map.Entry<String, Stream<Map.Entry<Date, Person>>>>() {
                    @Override
                    public Map.Entry<String, Stream<Map.Entry<Date, Person>>> apply(Map.Entry<String, List<Person>> stringListEntry) {
                        String country = stringListEntry.getKey();
                        Stream<Map.Entry<Date, Person>> dateByPersonStream = stringListEntry.getValue().stream()
                                .flatMap(new Function<Person, Stream<? extends Map.Entry<Date, Person>>>() {
                                    @Override
                                    public Stream<? extends Map.Entry<Date, Person>> apply(Person person) {
                                        return person.getAvailableDates().stream()
                                                .map(new Function<Date, Map.Entry<Date, Person>>() {
                                                    @Override
                                                    public Map.Entry<Date, Person> apply(Date date) {
                                                        return new AbstractMap.SimpleImmutableEntry(date, person);
                                                    }
                                                });
                                    }
                                });
                        return new AbstractMap.SimpleImmutableEntry(country, dateByPersonStream);
                    }
                })
                .map(new Function<Map.Entry<String, Stream<Map.Entry<Date, Person>>>, Map.Entry<String, Map<Date, List<Person>>>>() {
                    @Override
                    public Map.Entry<String, Map<Date, List<Person>>> apply(Map.Entry<String, Stream<Map.Entry<Date, Person>>> stringStreamEntry) {
                        String country = stringStreamEntry.getKey();
                        return new AbstractMap.SimpleImmutableEntry(country, stringStreamEntry.getValue()

                                .collect(Collectors.groupingBy(new Function<Map.Entry<Date, Person>, Date>() {
                                    @Override
                                    public Date apply(Map.Entry<Date, Person> datePersonEntry) {
                                        return datePersonEntry.getKey();
                                    }
                                }, Collectors.mapping(new Function<Map.Entry<Date, Person>, Person>() {
                                    @Override
                                    public Person apply(Map.Entry<Date, Person> datePersonEntry) {
                                        return datePersonEntry.getValue();
                                    }
                                }, Collectors.toList()))));
                    }
                })
                .map(new Function<Map.Entry<String, Map<Date, List<Person>>>, Map<String, Map<Date, List<Person>>>>() {
                    @Override
                    public Map<String, Map<Date, List<Person>>> apply(Map.Entry<String, Map<Date, List<Person>>> stringMapEntry) {
                        Map<String, Map<Date, List<Person>>> map = new HashMap<>();
                        map.put(stringMapEntry.getKey(), new TreeMap(stringMapEntry.getValue()));

                        return map;
                    }
                })
                .map(stringTreeMapMap -> stringTreeMapMap.entrySet().stream()
                        .map(new Function<Map.Entry<String, Map<Date, List<Person>>>, Conference>() {
                            @Override
                            public Conference apply(Map.Entry<String, Map<Date, List<Person>>> stringTreeMapEntry) {
                                Optional<Conference> optionalConference = stringTreeMapEntry.getValue().entrySet().stream()
                                        .map(new Function<Map.Entry<Date, List<Person>>, Conference>() {
                                            @Override
                                            public Conference apply(Map.Entry<Date, List<Person>> dateListEntry) {
                                                Conference conference = new Conference();
                                                conference.setCountry(stringTreeMapEntry.getKey());
                                                conference.setStartingDate(dateFormat.format(dateListEntry.getKey()));
                                                List<String> emails = new ArrayList<>();
                                                dateListEntry.getValue().forEach(new Consumer<Person>() {
                                                    @Override
                                                    public void accept(Person person) {
                                                        emails.add(person.getEmail());
                                                    }
                                                });

                                                conference.setEmails(emails);
                                                return conference;
                                            }
                                        })
                                        .max(new Comparator<Conference>() {
                                            @Override
                                            public int compare(Conference o1, Conference o2) {
                                                return o1.getEmails().size() - o2.getEmails().size();
                                            }
                                        });
                                return optionalConference.isPresent()? optionalConference.get(): null;
                            }
                        })
                        .collect(Collectors.toList()).get(0))
                .collect(Collectors.toList());

        gson.toJson(conferences, new FileWriter("out.json"));

    }


}
