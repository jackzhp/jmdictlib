# JMDict Parser
A convenience tool for parsing [JMDict](https://www.edrdg.org/jmdict/edict_doc.html), essentially a Japanese-English database

### Installing
Add the following to your build.gradle file

```
dependencies {
    // The actual package
    implementation 'com.github.danielneutrinos:jmdictlib:master-SNAPSHOT'
    
    // Additional package required for Android projects 
    // since android does not include StAX dependencies
    implementation 'stax:stax:1.2.0'
    ...
}
```

### How to use the package
It is recommended to initiate parsing in a separate thread so as to not block the UI if applicable.

An example written in kotlin
```
thread {
    val parser = JMDictParser(object: ParseEventListener {
        override fun entryParsed() {           
            updateProgress()
        }

        override fun completed() {
            showCompleted()            
        }
    })
    
    parser.parse()
    loadDictionary(parser.dictionary)
}
```

## Author
* **Daniel Li** - [DanielNeutrinos](https://github.com/DanielNeutrinos)

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments
* [The JMDict Project](https://www.edrdg.org/jmdict/j_jmdict.html)