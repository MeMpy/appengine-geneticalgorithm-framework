# appengine-geneticalgorithm-framework
Automatically exported from code.google.com/p/appengine-geneticalgorithm-framework

This is an old project of mine developed with an university collegue and friend for our master thesis.

Currently this project is deprecated because it uses a very old version of appengine and its master-slave datastore but the idea is still good and usefull.

With this framework you can build genetical alghoritm upon the google cloud and parallelize it using the map-reduce model. 

The idea was that the framework took care of the parallelization and rappresentation of the algorithm in a map-reduce model, the user must implements the chromosome and the optimization function. You can also specify some custom temination criteria.

This is the Getting Started Guide:

Iniziare ad usare il Framework

= Introduction =

In questa pagina spiegheremo come settare il proprio progetto per poter utilizzare il Framework


= Details =

<b>Primo passo: </b> <br>
Effettuare il checkout del progetto e compilarlo con ant.


<b>Secondo passo: </b> <br>

Nella cartella

<code> war/WEB-INF/ </code> 

inserire il seguente file:

<a href=http://code.google.com/p/appengine-geneticalgorithm-framework/source/browse/trunk/geneticalgorithmframework/war/WEB-INF/mapreduce.xml> mapreduce.xml </a>

lasciandolo invariato. <br>

<b> Terzo passo: </b> <br>

Modificare il proprio file <i> web.xml </i> inserendo il servlet mapping del framework come è mostrato nel seguente file:

<a href=http://code.google.com/p/appengine-geneticalgorithm-framework/source/browse/trunk/geneticalgorithmframework/war/WEB-INF/web.xml> web.xml </a>

<b> Quarto passo: </b> <br>

Inserire nella <i>root directory</i> dei <i>source file </i>, tipicamente <code> src/ </code> il file di configurazione del Framework:

<a href=http://code.google.com/p/appengine-geneticalgorithm-framework/source/browse/trunk/geneticalgorithmframework/exemple/geneticalgorithm.xml> geneticalgorithm.xml </a>
