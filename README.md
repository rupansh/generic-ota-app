# generic-ota-app
[![License: Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![KOTLIN](https://img.shields.io/badge/made%20with-kotlin-red.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)

*A zero BS OTA app for custom roms which doesn't require system app privileges*

## Requirements
- An Android Studio Environment
- Basic XML Understanding
- Basic App development experience
- A brain

## Setting-Up

- Rebrand the OTA app for your ROM if you wish to.
- Navigate to `app/src/main/res/drawable` and remove ic_toast.xml with your ROM's vector logo(Hint- use vector assist).

### For XDA-Fetch Mode (updater fetches all the data from xda thread):
- Navigate to `app/src/main/res/values/otasettings.xml`
- Change `fetchMode` to `xda`
- Navitgate to `app/src/main/res/values/xdadevices.xml`
- Remove the items from example devicearr and add items for  the devices your rom supports. The basic format is as follows-
`<item>devicecodename|xdathreadlink</item>`
- Change the dlprefix string to the one which your rom maintainers use for hosting roms. (eg. https://drive.google.com/ . It should pretty much be what your official download urls start with)
- Do check thread requirements for thread formatting

### For JSON Fetch Mode(Updater fetches data from a json file):
- Navigate to `app/src/main/res/values/otasettings.xml`
- Change `fetchMode` to `json`
- Navigate to `app/src/main/res/values/jsonurl.xml`
- Replace the string inside `devicesJSON` with the url of your json file
- Check JSON Requirements for format!

## JSON Requirements (Only for json fetch mode!)
- The format of the json file is as follows(Please make sure to properly follow the format)- 
```
[
    {
        "device" : "devicecodename1",
        "download" : "download_link_of_device1",
        "zip_name" : "name_of_the_zip1",
        "build_date" : "YYYY-MM-DD",
        "maintainer" : "maintainer1",
        "xda_thread" : "link_to_xda_thread1"
    },
    {
        "device" : "devicecodename2",
        "download" : "download_link_of_device2",
        "zip_name" : "name_of_the_zip2",
        "build_date" : "YYYY-MM-DD",
        "maintainer" : "maintainer2",
        "xda_thread" : "link_to_xda_thread2"
    }
]
```

- An example json file can be found here- [devices.json](https://gist.githubusercontent.com/rupansh/a9963fd372bb007365370b11e94610b0/raw/f72004278bec97258862e730a1ef0a598cc20341/devices.json)
- You can add as many devices as you want!
    
## Thread Requirements (Only for xda fetch mode!)

- It is **required** that you are posting your roms on **xda-forums**. After all, the app fetches **all the information** from the given xda thread!
- Your thread's **first** post **must** have a download link to the ROM
- Your thread **must** have the "Last Updated" text **actually up-to-date**. (i.e if the rom got updated today, the project's first post should be Last Updated: 2019-04-13)
- Your thread's title **should** be of the following format-
 **[SOME-INFO] [SOME-INFO] [ANOTHER-ONE] ROM TITLE [MORE INFO] [MAIBE?]**
    
    In a nutshell, the rom title should be **between []** (It is the default xda thread format that devs use so you probably already do that)
- Your thread's first post should **only** have **one** link with the download link prefix! Having them on **second/any other** post is just **fine**.

if you find this intimidating, it really isn't. Just look at this thread: https://forum.xda-developers.com/xiaomi-redmi-3s/development/rom-reloaded-caf-t3891208. or https://forum.xda-developers.com/xiaomi-redmi-3s/development/rom-pixel-experience-t3904334
Both of these threads are mostly ready for the app! (And many more in the Redmi 3s forum itself)

## Contributing

**Feel free to make a Pull Request! However before making one please make sure of the following:**

- All the code should be **compilable** and be **running** on **any** rom/device **with Android 9+**!
- All the code should be written in **kotlin**. I plan to keep this project purely kotlin.
- All the code should be properly formatted
- Please make sure that your Pull request is appropriate for a **generic** app. **ROM developers are expected to make some changes themselves**(Linking it with settings, Hiding it from launcher, customizing cardviews etc)

**Reporting Issues count as contributing too! Feel free to report them :)**


## TO-DO

- Add Inline Building support with AOSP(Probably with **Android.bp**)
- Feel free to make requests **appropriate** for a generic app in my **telegram** ( **@rupansh** )
