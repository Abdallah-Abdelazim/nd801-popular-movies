# Popular Movies
Second project in Udacity's Android Developer Nanodegree.  
## How to run the project?
This project uses web services from [TMDB][1].  
To run this project first request an API key from [TMDB][1] (If you don't know how follow this [guide][2]).  
Then open the global `gradle.properties` file located in:  
- In Linux and Mac machines it is located under `~/.gradle/`
- In Windows machines it can be found under `C:\Users\your_user_name\.gradle\`  

*If gradle.properties file is not present then create one*  

After that add the following line:  
```
TMDB_API_KEY="PUT_YOUR_TMDB_KEY_HERE"  
```

[1]: https://www.themoviedb.org
[2]: https://developers.themoviedb.org/3/getting-started/introduction
