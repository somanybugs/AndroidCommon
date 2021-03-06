# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-ignorewarnings

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep class org.xmlpull.v1.** { *; }

-keepattributes Signature
-keep class com.google.** { *; }

-keepclasseswithmembers  class * {
    native <methods>;
}

-keepclasseswithmembers  class * {
    public <init>(android.content.Context);
}

-keepclasseswithmembers  class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers  class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.google.gson.** {*;}
-keep class android.support.v4.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keep class * implements android.os.Parcelable{*;}
-keep class * implements java.io.Serializable{*;}
-keepclassmembers class * extends android.database.sqlite.SQLiteOpenHelper {
    public protected private <fields>;
    public protected <methods>;
}
#-keep class * implements java.io.Serializable{*;}
#umeng #########################################
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class com.umeng.fb.ui.ThreadView {
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepattributes Signature

-keep public class * {
    public protected <fields>;
    public protected <methods>;
}