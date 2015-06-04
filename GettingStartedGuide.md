Iniziare ad usare il Framework

# Introduction #

In questa pagina spiegheremo come settare il proprio progetto per poter utilizzare il Framework


# Details #

<b>Primo passo: </b> <br>
Effettuare il checkout del progetto e compilarlo con ant.<br>
<br>
<br>
<b>Secondo passo: </b> <br>

Nella cartella<br>
<br>
<pre><code> war/WEB-INF/ </code></pre>

inserire il seguente file:<br>
<br>
<a href='http://code.google.com/p/appengine-geneticalgorithm-framework/source/browse/trunk/geneticalgorithmframework/war/WEB-INF/mapreduce.xml'> mapreduce.xml </a>

lasciandolo invariato. <br>

<b> Terzo passo: </b> <br>

Modificare il proprio file <i> web.xml </i> inserendo il servlet mapping del framework come è mostrato nel seguente file:<br>
<br>
<a href='http://code.google.com/p/appengine-geneticalgorithm-framework/source/browse/trunk/geneticalgorithmframework/war/WEB-INF/web.xml'> web.xml </a>

<b> Quarto passo: </b> <br>

Inserire nella <i>root directory</i> dei <i>source file </i>, tipicamente <pre><code> src/ </code></pre> il file di configurazione del Framework:<br>
<br>
<a href='http://code.google.com/p/appengine-geneticalgorithm-framework/source/browse/trunk/geneticalgorithmframework/exemple/geneticalgorithm.xml'> geneticalgorithm.xml </a>