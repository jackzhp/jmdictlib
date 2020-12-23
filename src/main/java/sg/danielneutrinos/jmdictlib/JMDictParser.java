package sg.danielneutrinos.jmdictlib;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

import sg.danielneutrinos.jmdictlib.data.*;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import java.io.InputStream;
import java.util.*;

/**
 * Main class that handles parsing.
 */
public class JMDictParser {

    private XMLEventReader xmlEventReader;
    /**
     * Map to hold parsed data.
     * Map is keyed by {@link JMEntry#getEntrySequence()}
     */
    private Map<Integer, JMEntry> dictionary = new HashMap<>();

    /**
     * Callback listener. Implement for parsing events
     */
    private ParseEventListener parseEventListener;

    /**
     * Total number of entries in static jmdict xml
     */
    private static final int expectedDictSize = 185764;

    /**
     * Constructor to initialize
     *
     * @throws XMLStreamException thrown by parsing operation
     */
    public JMDictParser() throws XMLStreamException {
        System.setProperty("jdk.xml.entityExpansionLimit", "0");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("jmdict_e.xml");
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);
    }

    /**
     * Get dictionary map after parsing
     *
     * @return Map of entries with entry sequences as key
     */
    public Map<Integer, JMEntry> getDictionary() {
        return dictionary;
    }

    /**
     * Get dictionary in JSON format after parsing
     * @return List of entries in JSON format
     */
//    public String getDictionaryJSON() {
//        List<JMEntry> list = new ArrayList<>(dictionary.values());
//        Gson gson = new GsonBuilder().serializeNulls().create();
//        return gson.toJson(list);
//    }

    /**
     * Since source is a static file an expected number of entries can be obtained
     *
     * @return Expected number of entries
     */
    public static int getExpectedDictSize() {
        return expectedDictSize;
    }

    /**
     * Initiate the actual parsing operation. StAX limit for more than 64000 is removed, but YMMV
     *
     * @throws XMLStreamException can be thrown if changing setting property("jdk.xml.entityExpansionLimit") on jvm fails
     */
    @SuppressWarnings("unchecked")
    public void parse(ParseEventListener parseEventListener) throws XMLStreamException {
        if (xmlEventReader == null) throw new XMLStreamException("XMLEventReader not initialized");

        this.parseEventListener = parseEventListener;

        dictionary.clear();
        JMEntry entry = new JMEntry();
        KanjiElement kanjiElement = new KanjiElement();
        ReadingElement readingElement = new ReadingElement();
        Sense sense = new Sense();
        SourceLanguage sourceLanguage = new SourceLanguage();
        Gloss gloss = new Gloss();
        int sequence = -1;
        int index = 0;

        while (xmlEventReader.hasNext()) {
            XMLEvent nextEvent = xmlEventReader.nextEvent();

            if (nextEvent.isStartElement()) {
                StartElement startElement = nextEvent.asStartElement();
                switch (startElement.getName().getLocalPart().toLowerCase()) {
                    case "entry":
                        nextEvent = xmlEventReader.nextEvent();
                        entry = new JMEntry();
                        break;
                    case "ent_seq":
                        nextEvent = xmlEventReader.nextEvent();
                        sequence = Integer.parseInt(nextEvent.asCharacters().getData());
                        entry.setEntrySequence(sequence);
                        break;
                    case "k_ele":
                        nextEvent = xmlEventReader.nextEvent();
                        kanjiElement = new KanjiElement();
                        break;
                    case "keb":
                        nextEvent = xmlEventReader.nextEvent();
                        kanjiElement.setKanji(nextEvent.asCharacters().getData());
                        break;
                    case "ke_pri":
                        nextEvent = xmlEventReader.nextEvent();
                        kanjiElement.addPrimary(nextEvent.asCharacters().getData());
                        break;
                    case "ke_inf":
                        nextEvent = xmlEventReader.nextEvent();
                        kanjiElement.addInfo(nextEvent.asCharacters().getData());
                        break;
                    case "r_ele":
                        nextEvent = xmlEventReader.nextEvent();
                        readingElement = new ReadingElement();
                        break;
                    case "reb":
                        nextEvent = xmlEventReader.nextEvent();
                        readingElement.setKana(nextEvent.asCharacters().getData());
                        break;
                    case "re_restr":
                        nextEvent = xmlEventReader.nextEvent();
                        readingElement.addKanjiReading(nextEvent.asCharacters().getData());
                        break;
                    case "re_pri":
                        nextEvent = xmlEventReader.nextEvent();
                        readingElement.addPrimary(nextEvent.asCharacters().getData());
                        break;
                    case "re_inf":
                        nextEvent = xmlEventReader.nextEvent();
                        readingElement.addInfo(nextEvent.asCharacters().getData());
                        break;
                    case "re_nokanji":
                        nextEvent = xmlEventReader.nextEvent();
                        readingElement.setNotKanjiReading(true);
                        break;
                    case "sense":
                        nextEvent = xmlEventReader.nextEvent();
                        sense = new Sense();
                        break;
                    case "stagk":
                        nextEvent = xmlEventReader.nextEvent();
                        sense.addInvalidSenseKanji(nextEvent.asCharacters().getData());
                        break;
                    case "stagr":
                        nextEvent = xmlEventReader.nextEvent();
                        sense.addInvalidSenseReading(nextEvent.asCharacters().getData());
                        break;
                    case "xref":
                        nextEvent = xmlEventReader.nextEvent();
                        sense.addXReference(nextEvent.asCharacters().getData());
                        break;
                    case "ant":
                        nextEvent = xmlEventReader.nextEvent();
                        sense.addAntonym(nextEvent.asCharacters().getData());
                        break;
                    case "pos":
                        nextEvent = xmlEventReader.nextEvent();
                        sense.addPartOfSpeech(nextEvent.asCharacters().getData());
                        break;
                    case "field":
                        nextEvent = xmlEventReader.nextEvent();
                        sense.addField(nextEvent.asCharacters().getData());
                        break;
                    case "misc":
                        nextEvent = xmlEventReader.nextEvent();
                        sense.addMisc(nextEvent.asCharacters().getData());
                        break;
                    case "lsource":
                        nextEvent = xmlEventReader.nextEvent();
                        sourceLanguage = new SourceLanguage();

                        Iterator<Attribute> langAttributes = startElement.getAttributes();
                        while (langAttributes.hasNext()) {
                            Attribute attribute = langAttributes.next();
                            switch (attribute.getName().toString().toLowerCase()) {
                                case "lang":
                                    sourceLanguage.setLanguage(Locale.forLanguageTag(attribute.getValue()));
                                    break;
                                case "ls_wasei":
                                    sourceLanguage.setWaseiGo(attribute.getValue().equalsIgnoreCase("y"));
                                    break;
                                case "ls_type":
                                    sourceLanguage.setType(attribute.getValue());
                                    break;
                            }
                        }
                        break;
                    case "dial":
                        nextEvent = xmlEventReader.nextEvent();
                        sense.addDialect(nextEvent.asCharacters().getData());
                        break;
                    case "gloss":
                        nextEvent = xmlEventReader.nextEvent();
                        gloss = new Gloss();
                        gloss.setText(nextEvent.asCharacters().getData());

                        Iterator<Attribute> glossAttributes = startElement.getAttributes();
                        while (glossAttributes.hasNext()) {
                            Attribute attribute = glossAttributes.next();
                            switch (attribute.getName().toString().toLowerCase()) {
                                case "xml:lang":
                                    //Note: for jmdict in english it is always eng
                                    gloss.setLanguage(Locale.forLanguageTag(attribute.getValue()));
                                    break;
                                case "g_gend":
                                    gloss.setGender(attribute.getValue());
                                    break;
                                case "g_type":
                                    gloss.setType(attribute.getValue());
                                    break;
                            }
                        }
                        break;
                    case "pri":
                        nextEvent = xmlEventReader.nextEvent();
                        gloss.addPrimary(nextEvent.asCharacters().getData());
                        break;
                    case "s_inf":
                        nextEvent = xmlEventReader.nextEvent();
                        sense.setInfo(nextEvent.asCharacters().getData());
                        break;
                }
            }

            if (nextEvent.isEndElement()) {
                EndElement endElement = nextEvent.asEndElement();
                switch (endElement.getName().getLocalPart().toLowerCase()) {
                    case "entry":
                        dictionary.put(sequence, entry);
                        if (parseEventListener != null)
                            parseEventListener.entryParsed(index, entry);
                        index++;
                        break;
                    case "k_ele":
                        entry.addKanjiElement(kanjiElement);
                        break;
                    case "r_ele":
                        entry.addReadingElement(readingElement);
                        break;
                    case "sense":
                        entry.addSense(sense);
                        break;
                    case "lsource":
                        sense.addSourceLanguage(sourceLanguage);
                        break;
                    case "gloss":
                        sense.addGloss(gloss);
                        break;
                    default:
                }
            }
        }
        if (parseEventListener != null) parseEventListener.completed();
    }
}
