> **NOTE**: this project is long discontinued. It was my first big thing using Scala and Akka and (of course) when I look at it now I feel deeply embarassed of what I wrote all those years ago!

***

# GestureMatch
GestureMatch is a backend service that enables a new form of communication between mobile devices, that is extending hand gestures from one screen across into another. Imagine a swipe or pinch across two screens.

![](https://github.com/ticofab/gesturematch/blob/master-open/public/docs/swipe.png)

![](https://github.com/ticofab/gesturematch/blob/master-open/public/docs/pinch.png)

We built a startup on top of this technology, called CloudMatch - 
<http://cloudmatch.io>. The idea was to provide an SDK to integrate such functionality in mobile apps, and bill per usage. We met a lot of enthusiasm, but as it turned out, generate revenues was beyond our reach. 

The SDK and demos are available at the CloudMatch github account.

Applications that use GestureMatch are:

- [Perfect Earth Collector](https://play.google.com/store/apps/details?id=io.cloudmatch.perfectearth.cards): trade cards pinching across screens.
- [CloudMatch DEMO](https://play.google.com/store/apps/details?id=io.cloudmatch.demo&hl=en): shows the possibilities enabled by this technology.

This project makes use of WebSockets.

Please check the [GestureMatch wiki](https://github.com/ticofab/gesturematch/wiki) to learn how to build and use this project.

## LICENSE

This software is licensed under the Apache 2 license, quoted below.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with
the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
