# test

Input: We have a list of conference attendees. Each record contains attendee’s `firstName`, `lastName`, `email`, `country`, and `availableDates`  - array of dates when attendee is available to participate in the conference. 
Conference takes 2 consecutive dates. 
Attendee must participate both days. 

Task: For each country find a conference starting date when maximum number of attendees can participate in the conference. Build attendees list for each country. 
For example if attendee has Feb 23, Feb 24, Feb 25 & Feb 28 available - 2 conference start days are possible for him: Feb 23, and Feb 24. 
If there are two different starting dates with equal number of possible attendees were found - the earlier must have higher priority.
If country doesn't;’t have suitable date for the conference - do not include it in the result JSON.

So result JSON must be an array of objects with structure:

    {
      "country": "Russia",
      "startingDate": "2017-03-02",
      "emails": [
        "ivan@gmail.com",
        "sergey@mail.ru",
        "zhanna@yandex.ru",
        "maria@gmail.com"
      ]
    }
