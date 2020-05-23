# CollectionRound: Sum-safe Rounding for  Collections and Iterables
[ ![Download](https://api.bintray.com/packages/gilz688/CollectionRound/collection-round/images/download.svg) ](https://bintray.com/gilz688/CollectionRound/collection-round/_latestVersion)[![Build Status](https://travis-ci.org/gilz688/CollectionRound.svg?branch=master)](https://travis-ci.org/gilz688/CollectionRound)

CollectionRound is an Kotlin Multiplatform library for sum-safe rounding of floating point numbers for Kotlin/JVM, Kotlin/JS and Kotlin/Native (MinGW).

### Installation
Add to gradle dependencies
```groovy
implementation 'com.github.gilz688:collection-round:1.1'
```

### How to use Snapshot builds
Add to gradle repositories
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/gilz688/CollectionRound"
    }
}
```


### Supported Inputs

These are the inputs currently supported by the library. 

<li>Map</li>
<li>List</li>
<li>Array</li>
<li>Set</li>
<li>Iterable</li>

### Strategies

These are the strategies that can be used in rounding up the rounding errors. <li><b>DIFFERENCE</b> -  Adjust the items with the largest difference to preserve the sum. This is the default.</li>
<li><b>SMALLEST</b> - Adjust the smaller ones first.</li>
<li><b>LARGEST</b> - Adjust the largest ones first.</li>

### Example Code in Kotlin
The values in the map <b>chartData</b> are rounded off to 0 decimal place using the default strategy.
```kotlin
val chartData = mapOf(
    "apple" to 16.689,
    "banana" to 34.7022,
    "cherry" to 48.6088
)
val sum = chartData.values.sum()}
// sum = 100.0
val rounded = chartData.safeRound(0)
// rounded: {apple=17.0, banana=35.0, cherry=48.0}
val roundedSum = rounded.values.sum()
// roundedSum = 100.0, same as original sum
```

### Example Code in Java
The values in the list <b>data</b> are rounded off to 2 decimal places using the <b>SMALLEST</b> strategy while maintating the sum.
```java
List<Double> rounded = CollectionRoundKt.safeRound(data, 2, Strategy.SMALLEST);
```
