# Add project specific ProGuard rules here.

# Keep all data classes
-keep class com.example.ffridge.data.model.** { *; }
-keep class com.example.ffridge.data.local.entity.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Google Generative AI
-keep class com.google.ai.client.generativeai.** { *; }

# DataStore
-keep class androidx.datastore.*.** { *; }

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
