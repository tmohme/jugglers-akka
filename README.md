jugglers
========

Das projekt lässt sich mit SBT oder gradle bauen und starten.

Für ein `sbt run` muss SBT zuvor installiert sein.

Mit gradle geht es auch ohne vorherige Installation. Einfach im Projektverzeichnis `./gradlew run` eingeben. Wenn eine bereits eine passende gradle Version installiert ist wird sie genutzt, wenn nicht wird sie automatisch heruntergeladen und lokal im Projektverzeichnis installiert.

Wenn ihr SBT und Scala nicht schon laufen habt braucht ihr erstmal **Geduld**, bis die Scala Library und der Compile heruntergeladen wurden.

Mit gradle kann man zudem einfach eine installierbare Version der Anwendung einschließlich aller Dependencies bauen: `./gradlew distZip` erzeugt unter `build/distributions`ein entsprechendes Zip-File.

Mit `./gradlew installApp` kann die Anwendung direkt unter `build/install` installieren.

Viel Spaß beim ausprobieren :)
