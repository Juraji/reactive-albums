# Reactive Albums
Yey another version of the picture management/duplicate scan software I created a while back.  
This one is based on a Kotlin/Spring boot/Axon backend with a React frontend.

__Notice:__  
This is a passion and learning project, and I do not intent to make it commercially available.  
Updates come frequently and may break existing setups.  
Nonetheless, Feel free to open up an issue if you have any questions or request for information.

## How to use
_Note that as it stands now this app is not ready for full fledged production use._

### Stuff needed to run
* Java 14 or newer
* MariaDB 10 or newer _(Pro tip: use Docker!)_
* A web browser _(Chrome, Firefox and Opera are your best bet as of now)_

### Run it
* Setup MySQL with 4 databases:  
_Each of these use `root:secret` as login credentials by default. You can override this by overriding `application.yaml`._
  * `eventsourcing`
  * `projections`
  * `thumbnails`
  * `audit_log`
* Run the jar using `java -jar reactive-albums.jar`

## Building and Development

### Stuff needed for development
* An IDE capable of editing Kotlin, Typescript and running Maven projects.
* Knowledge of `Maven`, `Maven modules`, `Kotlin`, `Spring boot`, `Axon`, `Node`, `Webpack`, `TypeScript`, `React` and Optionally `Docker`.
* A MySQL server, set up like the one described in the "Run it" section.
* OpenJDK 14 or newer
* Node 12.x or newer
* Yarn

The Maven pom for the ui module has got an automatic setup for building using Node and Yarn, but development needs both installed globally.
