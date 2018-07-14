# Popular Movies
Second project in Udacity's Android Developer Nanodegree.

## Screenshots
![Navigation Drawer][3]
![Browse Movies Screen][4]
![Movie Details Screen 1][5]
![Movie Details Screen 2][6]

## Download
You can download the app APK from [here][7].

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
[3]: ./screenshots/1.png
[4]: ./screenshots/2.png
[5]: ./screenshots/3.png
[6]: ./screenshots/4.png
[7]: https://github.com/Abdallah-Abdelazim/Popular-Movies/releases/latest