# Open Fitbit Fitness Tracker Basic Example

If you want to create your own App follow this guidelines.

## Reference .jar libraries
1. Copy and paste the [.jar files](https://github.com/daniel-android-app-examples/open-fitbit-fitness-tracker-basicexample/tree/master/app/libs),
 **sxrblekeepfitaidl.jar** and **sxrblekeepfitlibrary_out.jar**, into app/libs folder of your Android Project.
2. Then, open your gradle.build(Module:app). There you can see .jar in dependencies{}
3. Go to **File>Project** **Structure>App>Dependencies>Add>jar** **Dependencies>libs** and Select both files.
4. Add a new Assets folder **File>New>Folder>Assets Folder** and copy the file [JySDK.xml](https://github.com/daniel-android-app-examples/open-fitbit-fitness-tracker-basicexample/tree/master/app/src/main/assets) 
in the folder **app->src->main->assets** that you just created through Android Studio.
5. The targetSdk version should be declared: defaultConfig in the build.gradle file. **Project Structure>App>Flavors>** minSdkVersion="18" and targetSdkVersion="19"

# Credits
Icons have been taken from [www.flaticon.com](www.flaticon.com)

Charts use [MPAndroid libraries](https://github.com/PhilJay/MPAndroidChart)
