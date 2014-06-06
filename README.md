## Sample CRUD application [![Build Status](https://travis-ci.org/nmartynenko/play-scala-style.png?branch=master)](https://travis-ci.org/nmartynenko/play-scala-style)

This is implementation of CRUD project by means of Playframework only and couple other Scala-frameworks.

####Play stack####
It doesn't have any IoC/DI containers, as well as any "JavaEE-specific" frameworks.
Beside play there is a stack of technologies:

- [Slick](http://slick.typesafe.com/) &mdash; Scala ORM, supported by Typesafe;

- [HyperSQL](http://hsqldb.org/) &mdash; embedded RDMBS;

- [Deadbolt 2 Scala Plugin](https://github.com/schaloner/deadbolt-2) &mdash; advanced security plugin for Play;

- [jBCrypt](http://www.mindrot.org/projects/jBCrypt/) &mdash; strong hashing function for security purposes;

And lots of things, which are provided by Play itself.