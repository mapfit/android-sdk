
#Mapfit SDK for Android


## Get your Mapfit API key
To use Mapfit Maps and services, your application will need an API key. You can get your API key for free [here](https://mapfit.com/getstarted).

## Install the Mapfit Android SDK
In your module level `build.gradle` file, add depencency as follows
```
dependencies {
    	compile 'com.mapfit.mapfitsdk:mapfit-android-sdk:1.0.0'
}
```

## Adding a map to your view
Inside your layout file, add `MapView` as follows

```xml
<com.mapfit.mapfitsdk.MapView
	        android:id="@+id/mapView"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent" />
```

## Initializing Mapfit
Before using `MapView` or any other Mapfit services, you need to instantiate a `Mapfit` instance as follows

#### Kotlin
```kotlin
Mapfit.getInstance(context, YOUR_API_KEY) 

``` 

#### Java
```java
Mapfit.getInstance(this, YOUR_API_KEY);
```

## Setup MapView and add your first Marker
To show the map and interact with it, you must setup the `MapView` as follows

#### Kotlin
```kotlin
mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
    override fun onMapReady(mapfitMap: MapfitMap) {
    
        val position = LatLng(40.744023, -73.993150)
        val marker= mapfitMap.addMarker(position)
        
    }
})
```

#### Java
```java
mapView.getMapAsync(new OnMapReadyCallback() {
  	@Override
  	public void onMapReady(MapfitMap mapfitMap) {
    
      	LatLng position = new LatLng(40.744023, -73.993150);
      	Marker marker = mapfitMap.addMarker(position);
        
    }
});
```

If you are looking for detailed documentations, see [Mapfit Documentation](https://mapfit-android.readme.io/docs)
