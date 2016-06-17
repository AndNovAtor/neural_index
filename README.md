NeuralIndex
===========

Project for indexing files by neuaral network.

by [AndNovAtor](https://github.com/AndNovAtor)

[Artificial neural network (ENG Wikipedia)](https://en.wikipedia.org/wiki/Artificial_neural_network)
[Artificial neural network (RUS Wikipedia)](https://ru.wikipedia.org/wiki/%D0%98%D1%81%D0%BA%D1%83%D1%81%D1%81%D1%82%D0%B2%D0%B5%D0%BD%D0%BD%D0%B0%D1%8F_%D0%BD%D0%B5%D0%B9%D1%80%D0%BE%D0%BD%D0%BD%D0%B0%D1%8F_%D1%81%D0%B5%D1%82%D1%8C)

Neural network classes with the Back-propagation teaching algorithm based on code of [Sovietmade](https://github.com/Sovietmade):
[CMakeList project on Github](https://github.com/Sovietmade/NeuralNetworks)

For lemmatisation in neural indexing [Standford CoreNLP](http://stanfordnlp.github.io/CoreNLP) library is used.

## Dependencies

Almost all libraries was added to Git. For example, [JUnit](http://junit.org/) tests, [slf4j](http://www.slf4j.org) for logs, also Stansford CoreNLP dependences library. But not models library of Stansford CoreNLP ('cos it has size > 300 MB). It can be downloaded from [here](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22stanford-corenlp%22). It's not direct link. There are few models - big files for each language (for example, stanford-corenlp-x.x.x-models-english.jar, has ~900 MB). However, there is smaller file, that used with this project - stanford-corenlp-3.6.0-models.jar (~300 MB), direct link for it: [link](https://repo1.maven.org/maven2/edu/stanford/nlp/stanford-corenlp/3.6.0/stanford-corenlp-3.6.0-models.jar)

## Licence

This project has GPLv3 licence. More because of the Standford CoreNLP license (GPLv3).