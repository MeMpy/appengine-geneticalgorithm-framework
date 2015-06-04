# appengine-geneticalgorithm-framework
Automatically exported from code.google.com/p/appengine-geneticalgorithm-framework

This is an old project of mine developed with an university collegue and friend for our master thesis.

Currently this project is deprecated because it uses a very old version of appengine and its master-slave datastore but the idea is still good and usefull.

With this framework you can build genetical alghoritm upon the google cloud and parallelize it using the map-reduce model. 

The idea was that the framework took care of the parallelization and rappresentation of the algorithm in a map-reduce model, the user must implements the chromosome and the optimization function. You can also specify some custom temination criteria.

This is the Getting Started Guide:


<b>First Step: </b> <br>
Clone the project and build with ant.


<b>Second Step: </b> <br>

In the folder

<code> war/WEB-INF/ </code> 

insert the file:

<a href=https://github.com/MeMpy/appengine-geneticalgorithm-framework/blob/master/geneticalgorithmframework/war/WEB-INF/mapreduce.xml> mapreduce.xml </a>

without change it. <br>

<b> Third step: </b> <br>

Modify your  <i> web.xml </i> inserting the framework servlet mapping as follows:

<a href=https://github.com/MeMpy/appengine-geneticalgorithm-framework/blob/master/geneticalgorithmframework/war/WEB-INF/web.xml> web.xml </a>

<b> Fourth step: </b> <br>

Insert in the <i>source root directory</i>, typically <code> src/ </code> the framework configuration file:

<a href=https://github.com/MeMpy/appengine-geneticalgorithm-framework/blob/master/geneticalgorithmframework/exemple/geneticalgorithm.xml> geneticalgorithm.xml </a>
