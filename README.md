# generic-ota-app

A zero BS OTA app for custom roms which doesn't require system app privellages

## Requirements
- An Android Studio Environment
- Basic Kotlin Skills
- Basic XML Understanding
- Basic App development experience
- A brain

## Setting-Up

- Navitgate to `app/src/main/res/values/devicestrings.xml`
- Remove the items from example devicearr and add items for  the devices your rom supports. The basic format is as follows-
`<item>devicecodename|xdathreadlink</item>`
- Change the dlprefix string to the one which your rom maintainers use for hosting roms. (eg. https://drive.google.com/ . It should pretty much be what your official download urls start with)
- Rebrand the OTA app for your ROM if you wish to.
- Navigate to `app/src/main/res/drawable` and remove ic_toast.xml with your ROM's vector logo(Hint- use vector assist). PS:- The logo size should strictly be 430x180dp

## Thread Requirements

- It is **required** that you are posting your roms on **xda-forums**. After all, the app fetches **all the information** from the given xda thread!
- Your thread's **first** post **must** have a download link to the ROM
- Your thread **must** have the "Last Updated" text **actually up-to-date**. (i.e if the rom got updated today, the project's first post should be Last Updated: 2019-04-13)
- Your thread's title **should** be of the following format-
 **[SOME-INFO] [SOME-INFO] [ANOTHER-ONE] ROM TITLE [MORE INFO] [MAIBE?]**
    
    In a nutshell, the rom title should be **between []** (It is the default xda thread format that devs use so you probably already do that)
- Your thread's first post should **only** have **one** link with the download link prefix! Having them on **second/any other** post is just **fine**.

**Some Alternatives to the requirements-**

- You can modify the app's **networkfetching** and **ScrollingActivity** to support other forums
- You can modify the app's **networkfetching** to fetch the link from any post
- You can modify the app to use parse api or json to get the latest build date in the format. An example using parse can be found in this commit - [https://github.com/rupansh/generic-ota-app/commit/eda9ff99858117c4b920bed266b9b7d4c39e06ab#diff-05efa0086bca94e2fc4da01cf12e3dbeR53](https://github.com/rupansh/generic-ota-app/commit/eda9ff99858117c4b920bed266b9b7d4c39e06ab#diff-05efa0086bca94e2fc4da01cf12e3dbeR53) I haven't used json files but its quite easy to use! The point of this app is to fetch maximum the information from the xda thread itself anyways
- You can modify the thread title parsing in **networkfetching** but I recommend you to follow the general format instead!
-  You can modify **getDeviceLink()** in **networkfetching** to put all the links starting with the prefix in a list and then return the appropriate link according to the indice

## Contributing

**Feel free to make a Pull Request! However before making one please make sure of the following:**

- All the code should be **compilable** and be **running** on **any** rom/device **with Android 9+**!
- All the code should be written in **kotlin**. I plan to keep this project purely kotlin.
- All the code should be properly formatted
- Please make sure that your Pull request is appropriate for a **generic** app. **ROM developers are expected to make some changes themselves**(Linking it with settings, Hiding it from launcher, customizing cardviews etc)

**Reporting Issues count as contributing too! Feel free to report them :)**


## TO-DO

- Add Inline Building support with AOSP(Probably with **Android.bp**)
- Add a **background service** which auto checks for updates every n hours and sends a notification
- Feel free to make requests **appropriate** for a generic app in my **telegram** ( **@rupansh** )
