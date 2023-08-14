package de.krieger.personal.contractgenerator.service;

import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import de.krieger.personal.contractgenerator.model.Candidate;
import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.model.Signee;
import de.krieger.personal.contractgenerator.repository.CandidateRepository;
import de.krieger.personal.contractgenerator.service.impl.PdfLayoutServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PdfServiceImplTest {

    @InjectMocks
    private PdfLayoutServiceImpl pdfLayoutService;
    @Mock
    private CandidateRepository candidateRepository;

    @BeforeAll
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFile() throws IOException {
        Candidate candidate = new Candidate();
        candidate.setFirstName("Max");
        candidate.setLastName("Mustermann");
        candidate.setStreet("Musterstraße");
        candidate.setStreetNumber("19");
        candidate.setZipCode("10437");
        candidate.setResidence("Musterhausen");
        candidate.setCandidateId(1L);
        candidate.setSalutation("Herrn");
        Mockito.when(candidateRepository.save(Mockito.any(Candidate.class))).thenReturn(candidate);
        Mockito.when(candidateRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(candidate));
        candidateRepository.save(candidate);
        Contract contract = new Contract();
        contract.setTemplate("|<c>*Gültig für Mitarbeiter [contractVersionName]*\n" +
                "|<t>*Arbeitsvertrag*\n\n" +
                "|Zwischen der |<44>*[workplace]*\n" +
                "|<106>*[location]*\n\n" +
                "|<106>als Arbeitgeber\n\n" +
                "|<0>und |<86>*[salutation] [firstName] [lastName]*\n" +
                "|<106>Anschrift: *[street] [streetNumber], [zipCode] [residence]*\n\n" +
                "|<106>als Arbeitnehmer " +
                "|<s>_- die männliche Bezeichnung „Arbeitnehmer“ wird\n " +
                "aus Gründen der sprachlichen Vereinfachung\n " +
                "gewählt; die Bestimmungen dieses Vertrages\n " +
                "gelten ungeachtet dieser Bezeichnung für\n " +
                "Beschäftigte jeden Geschlechts - _\n\n " +
                "|<0>wird folgender Arbeitsvertrag geschlossen:\n\n\n" +
                "|<c>*§ 1 Tätigkeit, Befristung, Probezeit, Bedingungen*\n\n" +
                "|" +
                "(1) Die Einstellung als *[jobTitle]* erfolgt ab *[startDate]*\n" +
                "\n" +
                "Optional:\n" +
                "|<30>#__*Aufschiebende Bedingung Betriebsratszustimmung:__*\n" +
                "unter der aufschiebenden Bedingung, dass der für den Betrieb zustädnige Betriebsrat der " +
                "Einstellung des Arbeitnehmers zustimmt; der Arbeitnehmer wurde insoweit darauf " +
                "hingewiesen, dass der Betriebsrat diese Zustimmung noch nicht erteilt hat. Der Arbeitgeber ist " +
                "berechtigt, aber nicht verpflichtet, gegen eine etwaige Zustimmungsverweigerung des " +
                "Betriebsrats gerichtliche Schritte einzuleiten. Der Arbeitgeber ist verpflichtet, dem " +
                "Arbeitnehmer unverzüglich mitzuteilen, dass ein Arbeitsverhältnis nicht zustande kommt, " +
                "wenn der Betriebsrat die Zustimmung verweigert und das Arbeitsgericht die Zustimmung nicht " +
                "ersetzt oder der Arbeitgeber die Ersetzung nicht beantragt.\n\n" +
                "|<30>#__*Aufschiebende Bedingung Ausländer:*__\n" +
                "unter der aufschiebenden Bedingung, dass der Arbeitnehmer vor dem X1 einen gültigen " +
                "Aufenthaltstitel, der ihn zur Erwerbstätigkeit berechtigt, vorlegt. Legt der Arbeitnehmer einen " +
                "solchen Aufenthaltstitel erst am oder nach dem X1, aber spätestens bis zum X1+3M vor, " +
                "beginnt das Arbeitsverhältnis an dem Tag nach der Vorlage des Aufenthaltstitels, wenn dieser " +
                "ein Sonntag ist, dann am folgenden Montag. Legt der Arbeitnehmer den Aufenthaltstitel erst " +
                "nach dem X1+3M vor, kommt kein Arbeitsverhältnis zustande.\n\n" +
                "|<30>#__*Auflösende Bedingung Ausländer:__*\n" +
                "|<50>$__Wenn Arbeitsvertrag unbefristet:__ Das Arbeitsverhältnis beginnt am DATUM 1. Es wird " +
                "auf unbestimmte Zeit geschlossen, *steht aber unter der Bedingung*, dass der " +
                "Arbeitnehmer sich in Deutschland nicht nur aufhalten, sondern auch einer " +
                "Erwerbstätigkeit nachgehen darf, mit der Folge, dass das *Arbeitsverhältnis endet,* " +
                "*ohne dass es einer Kündigung bedarf*, wenn der zur Erwerbstätigkeit " +
                "berechtigende Titel aufgehoben wird oder die darin genannte Frist – DATUM 2 wie im " +
                "Aufenthaltstitel - abläuft, ohne dass eine Verlängerung erteilt und diese der " +
                "Arbeitgeberin spätestens am Fristablauftag, das heißt dem DATUM 2 wie im " +
                "Aufenthaltstitel nachgewiesen wird.\n" +
                "|<50>$__Wenn Arbeitsvertrag befristet:__ Das Arbeitsverhältnis beginnt am DATUM 1. Es ist " +
                "*befristet* und endet spätestens am DATUM 3, ohne dass es einer Kündigung bedarf. " +
                "*Zudem steht es unter der Bedingung*, dass der Arbeitnehmer sich in Deutschland " +
                "nicht nur aufhalten, sondern auch einer Erwerbstätigkeit nachgehen darf, mit der " +
                "Folge, dass das *Arbeitsverhältnis endet, ohne dass es einer Kündigung bedarf,* " +
                "wenn der zur Erwerbstätigkeit berechtigende Titel aufgehoben wird oder die darin " +
                "genannte Frist – DATUM 2 wie im Aufenthaltstitel - abläuft, ohne dass eine " +
                "Verlängerung erteilt und diese der Arbeitgeberin spätestens am Fristablauftag, das " +
                "heißt dem DATUM 2 wie im Aufenthaltstitel nachgewiesen wird.\n\n" +
                "|<30>#__*Auflösende Bedingung Werkstudent:*__ Das Arbeitsverhältnis steht unter der auflösenden " +
                "Bedingung, dass der Arbeitnehmer als Vollzeitstudent immatrikuliert ist und seine letzte " +
                "Prüfungsleistung noch nicht erbracht hat, mit der Folge, dass das Arbeitsverhältnis endet, " +
                "ohne dass es einer Kündigung bedarf, wenn das Vollzeitstudium beendet ist. Der " +
                "Arbeitnehmer verpflichtet sich, den Arbeitgeber sofort zu informieren, wenn sich sein " +
                "beruflicher Status ändert.\n\n" +
                "|<30>#__*Befristung:*__ befristet bis zum *[limitDate]*.\n\n" +
                "|<0>(2) Die ersten [max 6] Monate gelten als Probezeit. Während dieser Probezeit kann das " +
                "Arbeitsverhältnis beidseitig mit der besonderen Frist des § 9 Abs. 1 gekündigt werden.\n\n" +
                "|<0>Optional:\n" +
                "|<30>#(3) Mit Fristende endet das Arbeitsverhältnis, ohne dass es einer Kündigung bedarf.\n" +
                "|<30>#Der Arbeitnehmer ist verpflichtet, sich spätestens drei Monate vor Beendigung des " +
                "Arbeitsvertrages bei der zuständigen Agentur für Arbeit arbeitsuchend zu melden. Eine " +
                "verspätete Meldung kann zu einer Reduzierung des Arbeitslosengeldanspruches führen. Auf " +
                "Grund verspäteter Meldungen können keine Schadensersatzforderungen gegen den " +
                "Arbeitgeber geltend gemacht werden.\n\n" +
                "|<c>*§ 2 Arbeitsort, Aufgaben*\n\n" +
                "|" +
                "(1) Der Arbeitsort ist die Betriebsstätte *[workplace]*.\n\n" +
                "(2) Der Arbeitgeber ist berechtigt, die vertraglich geschuldete Tätigkeit durch eine Stellenbeschreibung " +
                "zu konkretisieren.\n\n" +
                "(3) Der Arbeitnehmer erklärt sich ferner bereit, vorübergehend für die Dauer von [1 oder 2] Monat[en] " +
                "aushilfsweise andere zumutbare Tätigkeiten, auch an anderen Orten auszuüben. Dies gilt auch nach " +
                "langjähriger unveränderter Tätigkeit.\n\n" +
                "|<c>*§ 3 Vertragsbestandteile*\n\n" +
                "|" +
                "(1) Bestandteil des Arbeitsvertrages ist der vom Arbeitnehmer ausgefüllte Personalbogen. Mit der " +
                "Unterschrift unter diesen Vertrag erklärt der Arbeitnehmer, dass alle Angaben zu seiner Person " +
                "vollständig und richtig sind. Wissentlich falsche Angaben können die Anfechtung bzw. die fristlose " +
                "Kündigung des Arbeitsverhältnisses begründen.\n\n" +
                "(2) Im Übrigen gelten die Allgemeinen Arbeitsbedingungen, die Betriebsordnung und Anweisungen in " +
                "ihren jeweils gültigen Fassungen.\n\n" +
                "|<c>*§ 4 Vergütung*\n\n" +
                "|" +
                "(1) Die Vergütung beträgt *[salary],00 EUR* brutto/Monat.\n\n" +
                "*Optional Prämie:*\n" +
                "Zusätzlich erhält der Arbeitnehmer als weitere Vergütung bei Erreichen der jeweiligen\n" +
                "Voraussetzungen und/oder Ziele eine Prämie gemäß der gesonderten Anlage zu diesem Vertrag.\n\n" +
                "(2) Mit dieser Vergütung sind alle Tätigkeiten des Arbeitnehmers im Rahmen des Arbeitsverhältnisses\n" +
                "abgegolten. Dies gilt auch für eine etwaige Arbeit an Samstagen sowie Sonn- und Feiertagen sowie\n" +
                "die Leistung von Schichtarbeit.\n\n" +
                "(3) Ebenso mit dieser Vergütung abgegolten sind Überstunden im Umfang von 10% der regelmäßigen " +
                "Arbeitszeit. Dies ist bei der Abrechnung des Arbeitszeitkontos zu berücksichtigen.\n\n" +
                "|<c>*§ 5 Freiwillige Leistungen*\n\n" +
                "|" +
                "(1) Sämtliche Leistungen, die vom Arbeitgeber zusätzlich zu der in § 4 geregelten Vergütung erbracht " +
                "werden, sind freiwillige Leistungen des Unternehmens, auf die ein Anspruch nicht besteht und aus der " +
                "auch bei wiederholter Zahlung eine betriebliche Übung nicht abgeleitet werden kann. Dies gilt " +
                "namentlich für Gratifikationen, Prämien und sonstige Sonderleistungen. \n\n" +
                "(2) Sollte das Unternehmen freiwillige Leistungen erbringen, sind Mitarbeiter ausgeschlossen während " +
                "der Elternzeit, Wehr- oder Ersatzdienst und in allen Fällen, in denen das Anstellungsverhältnis ruht. \n\n" +
                "(3) Freiwillige Leistungen werden auch bei Unterbrechungen des Arbeitsverhältnisses von mehr als " +
                "sechs Wochen innerhalb des Leistungsszeitraums, während derer kein Anspruch auf " +
                "Entgeltfortzahlung besteht, entsprechend der Dauer der Unterbrechung gekürzt.\n\n" +
                "|<c>*§ 6 Kollektivvereinbarungen (Betriebsvereinbarungen, Tarifverträge)*\n\n" +
                "|" +
                "(1) Im Betrieb geltende Kollektivvereinbarungen gehen den Bestimmungen dieses Arbeitsvertrages in " +
                "jedem Fall vor. Dies gilt zunächst für im Betrieb geschlossene Betriebsvereinbarungen und " +
                "Regelungsabreden. Die Parteien sind sich darüber einig, dass die jeweils gültigen einschlägigen " +
                "Betriebsvereinbarungen sowie die getroffenen Regelungsabreden Anwendung finden und für die " +
                "Dauer ihrer Geltung den Regelungen in diesem Vertrag auch dann vorgehen, wenn die vertragliche " +
                "Regelung im Einzelfall günstiger ist. \n\n" +
                "(2) Derzeit findet kein Tarifvertrag auf das Arbeitsverhältnis Anwendung. Falls zukünftig ein Tarifvertrag " +
                "zwingend gelten sollte, treten an die Stelle der entsprechenden Regelungen dieses Vertrages und der " +
                "Betriebsvereinbarungen bzw. Regelungsabreden oder Gesamtzusagen ausschließlich die tariflichen " +
                "Regelungen. Der Mitarbeiter kann sich auf etwa günstigere Regelungen aus diesem Vertrag, aus " +
                "Betriebsvereinbarungen oder Regelungsabreden während der Zeit der Tarifbindung nicht berufen. Alle " +
                "freiwilligen Leistungen werden für den Zeitraum der Tarifbindung außer Kraft gesetzt, es sei denn, " +
                "diesbezüglich wird eine andere Festlegung getroffen. Dies gilt insbesondere für den Fall der " +
                "zwingenden Geltung eines Lohn- bzw. Gehaltstarifvertrages; für den Zeitraum einer solchen " +
                "Tarifbindung des Arbeitgebers (Dauer einer Allgemeinverbindlicherklärung bzw. Laufzeit des " +
                "verbindlichen Tarifvertrages) bemisst sich die Höhe der Vergütung ausschließlich nach den tariflichen " +
                "Regelungen. Das Günstigkeitsprinzip ist insoweit ausgeschlossen. \n\n" +
                "(3) Im Falle der Anwendung eines Tarifvertrages müssen die tariflichen Mindestleistungen innerhalb " +
                "eines Kalenderjahres erbracht sein. Mindervergütungen in einzelnen Monaten können mit " +
                "übertariflichen Leistungen in anderen Monaten verrechnet werden. Sollte das Unternehmen während " +
                "einer Tarifbindung über- oder außertarifliche Leistungen erbringen, sind diese auf die tariflichen " +
                "Leistungen, Tariferhöhungen, Höhergruppierungen anrechenbar. \n\n" +
                "( 4 ) N a c h A b l a u f e i n e r B i n d u n g a n K o l l e k t i v v e r e i n b a r u n g e n ( W e g f a l l e i n e r " +
                "Allgemeinverbindlicherklärung bzw. Ablauf des Tarifvertrages oder Ablauf einer Betriebsvereinbarung " +
                "oder Entfall einer Rgelungsabrede) wird hiermit vereinbart, dass die während der Anwendung " +
                "kollektivrechtlicher Regelungen verdrängten Bestimmungen dieses Arbeitsvertrages in vollem Umfang " +
                "wieder aufleben. Das gleiche gilt für sonstige Vereinbarungen aus und im Zusammenhang mit dem " +
                "Arbeitsverhältnis, soweit diese durch anwendbare Tarifbestimmungen ganz oder teilweise verdrängt " +
                "waren. Eine etwaige Nachwirkung von Tarifverträgen bzw. von Betriebsvereinbarungen und eine " +
                "betriebliche Übung werden hiermit ausgeschlossen. Insbesondere richtet sich die Vergütung des " +
                "Arbeitnehmers nach Ablauf einer Tarifbindung wieder ausschließlich nach § 4.\n\n" +
                "|<c>*§ 7 Arbeitszeit*\n\n" +
                "|" +
                "(1) Der Arbeitgeber und der Arbeitnehmer sind sich darüber einig, dass die regelmäßige monatliche " +
                "Arbeitszeit\n\n" +
                "|<c>[workingHours] Stunden\n\n" +
                "|beträgt und zwar unabhängig von den eventuell zukünftig geltenden tariflichen Bestimmungen. \n\n" +
                "(2) Liegt diese Arbeitszeit über der regelmäßigen Arbeitszeit, die sich aus einem für den Arbeitgeber " +
                "verbindlichen Manteltarifvertrag ergibt und ist für den Arbeitgeber zugleich ein Lohn- bzw. " +
                "Gehaltstarifvertrag verbindlich, dann hat der Arbeitnehmer Anspruch darauf, dass sich die nach dem " +
                "Lohn- bzw. Gehaltstarifvertrag bemessene Mindestvergütung um den der Mehrarbeit entsprechenden " +
                "Anteil erhöht. Nach Ablauf der Tarifbindung (Wegfall einer Allgemeinverbindlichkeitserklärung bzw. " +
                "Ablauf des Tarifvertrages) gilt § 6 Abs. 4 dieses Vertrages.\n" +
                "\n" +
                "(3) Der Arbeitnehmer ist bei entsprechendem betrieblichem Bedarf bereit, auch Mehr- und " +
                "Überstunden zu leisten. Überstunden werden nur dann anerkannt, wenn sie vorher schriftlich mit dem " +
                "Arbeitgeber vereinbart bzw. durch diesen angeordnet wurden. Darüber hinaus ist der Arbeitnehmer " +
                "verpflichtet, Beginn und Ende der Überstunden und Mehrarbeit täglich schriftlich zu erfassen und " +
                "diese spätestens am Ende der Kalenderwoche dem Arbeitgeber vorzulegen, wenn im Betrieb generell " +
                "oder für einzelne Tage keine elektronische Zeiterfassung möglich ist; anderenfalls gilt §8. \n\n" +
                "(4) Die Verteilung der Arbeitszeit erfolgt nach den Vorgaben des Arbeitgebers flexibel entsprechend " +
                "den betrieblichen Erfordernissen. \n\n" +
                "(5) Der Arbeitgeber ist berechtigt, dem Arbeitnehmer nach billigem Ermessen feste Pausenzeiten " +
                "vorzugeben, für die keine Vergütungspflicht besteht; dabei ist auch eine Vorgabe von Pausen " +
                "zulässig, die die Dauer der gesetzlichen Mindestpausen übersteigt. Soweit eine solche Vorgabe nicht " +
                "besteht, gelten die gesetzlichen Pausenbestimmungen. Der Arbeitnehmer ist verpflichtet, Pausen " +
                "entsprechend der geltenden Vorgabe einzulegen. Diese werden im Rahmen der Zeiterfassung " +
                "automatisch pauschaliert in Abzug gebracht, ohne dass der Arbeitgeber Dauer und Lage der " +
                "tatsächlichen Inanspruchnahme von Pausen erfasst oder prüft. \n\n" +
                "(6) Der Arbeitgeber ist berechtigt, Kurzarbeit anzuordnen, wenn ein erheblicher, auf wirtschaftlichen " +
                "Gründen oder einem unabwendbaren Ereignis beruhender Arbeitsausfall vorliegt, und er dies bei der " +
                "Agentur für Arbeit anzeigt. Im Fall der Anordnung von Kurzarbeit ist der Arbeitnehmer mit der " +
                "vorübergehenden Verkürzung seiner individuellen Arbeitszeit sowie der dementsprechenden " +
                "Reduzierung seiner Vergütung einverstanden, wenn und soweit die Voraussetzungen für die " +
                "Gewährung von Kurzarbeitergeld erfüllt sind. Bei vollständigem Arbeitsausfall können die Arbeitszeit " +
                "und dementsprechend auch die Vergütung auf Null herabgesetzt werden (Kurzarbeit Null). Der " +
                "Arbeitgeber hat dem Arbeitnehmer gegenüber bei der Anordnung von Kurzarbeit eine " +
                "Ankündigungsfrist von zwei Wochen einzuhalten; diese Ankündigungsfrist kann durch eine " +
                "Betriebsvereinbarung abgekürzt werden.\n\n" +
                "|<c>*§ 8 Arbeitszeitkonto*\n\n" +
                "|" +
                "(1) Der Arbeitgeber führt für den Arbeitnehmer ein individuelles Arbeitszeitkonto. In diesem werden die " +
                "tatsächlich geleisteten Arbeitszeiten erfasst und mit der regelmäßigen Arbeitszeit nach § 7 saldiert. " +
                "Dabei kann das Arbeitszeitkonto bis zum Umfang von 200 Arbeitsstunden auch im Soll belastet " +
                "werden; der Arbeitnehmer ist in diesem Fall zur Nacharbeit verpflichtet. Zeiten der Entgeltfortzahlung " +
                "(insbesondere Krankheit, Urlaub) werden mit der vereinbarten täglichen Arbeitszeit erfasst (Ist = Soll). " +
                "Zeiten des Ruhens des Arbeitsverhältnisses bleiben für das Arbeitszeitkonto neutral.\n\n" +
                "(2) Der Arbeitgeber ist berechtigt, bei der Erfassung der tatsächlich geleisteten Arbeitszeiten unter " +
                "Berücksichtigung der konkreten betrieblichen Anforderungen, insbesondere der innerbetrieblichen " +
                "Wege- und vergleichbarer Zeiten, in angemessenem Umfang Kappungsgrenzen und Schwellenwerte " +
                "einzuführen und/oder Rundungen vorzunehmen. Soweit für den Arbeitnehmer eine betriebliche " +
                "Arbeitszeiteinteilung oder Schichtplanung des Arbeitgebers besteht, ist der Arbeitgeber ferner " +
                "berechtigt, angemessene automatische Kappungen der betrieblichen Anwesenheitszeiten " +
                "vorzusehen. Über solche Kappungen hinausgehende manuelle Erfassungen bedürfen grundsätzlich " +
                "der vorherigen Zustimmung des Vorgesetzten.\n\n" +
                "(3) Das Arbeitszeitkonto wird jeweils zum Ende eines Kalenderjahres abgerechnet. Bei unterjährigem " +
                "Ausscheiden des Arbeitnehmers erfolgt die Abrechnung spätestens bei Beendigung des " +
                "Arbeitsverhältnisses. Bei Abrechnung des Arbeitszeitkontos wird geprüft, ob der Arbeitnehmer im " +
                "zurückliegenden Kalenderjahr insgesamt eine Vergütung (einschließlich etwaiger " +
                "Ausgleichszahlungen auf die Mindestvergütung) erhalten hat, die mindestens der Summe der " +
                "tatsächlichen Arbeitsstunden multipliziert mit dem jeweiligen gesetzlichen Mindestlohn entspricht. " +
                "Sollte dies nicht der Fall sein, so erfolgt zugunsten des Arbeitnehmers eine entgeltliche Abgeltung so " +
                "vieler Arbeitsstunden aus dem Arbeitszeitkonto, dass der gesetzliche Mindestlohnanspruch erfüllt ist. " +
                "Im Falle des unterjährigen Ausscheidens gilt als zurückliegendes Kalenderjahr das laufende " +
                "Kalenderjahr bis zum Zeitpunkt des Ausscheidens.\n\n" +
                "(4) Ein nach Abrechnung und etwaiger Abgeltung gemäß Absatz 3 verbleibender Habensaldo des " +
                "Arbeitszeitkontos wird um die gemäß § 4 Abs. 3 im zurückliegenden Abrechnungszeitraum bereits mit " +
                "der laufenden Vergütung abgegoltenen Guthabenstunden gekürzt; der Aufbau eines Sollsaldos durch " +
                "diese Kürzung ist ausgeschlossen. Ein hiernach noch verbleidender Soll- oder Habensaldo wird als " +
                "Anfangssaldo in das Arbeitszeitkonto des nachfolgenden Abrechnungszeitraums übertragen. \n\n" +
                "(5) Bei Guthaben des Arbeitszeitkontos handelt es sich um reine Zeitguthaben, die durch Freizeit " +
                "abzubauen sind. Der Arbeitgeber kann einen Freizeitausgleich im Rahmen des § 106 GewO auch " +
                "einseitig anordnen. Außer in den Fällen des Absatzes 3 besteht kein Anspruch auf Abgeltung von " +
                "Zeitguthaben durch zusätzliche Vergütung. \n\n" +
                "(6) Betriebsvereinbarungen über Arbeitszeitkonten gehen den vorstehenden Bestimmungen vor (§ 6).\n\n" +
                "|<c>*§ 9 Beendigung des Arbeitsverhältnisses*\n\n" +
                "|" +
                "(1) Das Arbeitsverhältnis ist jederzeit ordentlich kündbar; dies gilt auch während einer etwaigen " +
                "Befristung. Während einer vereinbarten Probezeit (§ 1 Abs. 2) beträgt die Kündigungsfrist 2 Wochen. " +
                "Im Übrigen gelten die Kündigungsfristen des § 622 BGB. Das Arbeitsverhältnis kann auch schon vor " +
                "seinem Beginn gekündigt werden. Die Kündigungsfrist beginnt in diesem Fall mit dem Zugang der " +
                "Kündigung. \n\n" +
                "(2) Eine fristlose Kündigung des Arbeitsverhältnisses ist bei Vorliegen eines wichtigen Grundes " +
                "jederzeit möglich. Sollte eine fristlose Kündigung unwirksam sein, so gilt diese als fristgemäße " +
                "Kündigung zum nächsten zulässigen Kündigungstermin. \n\n" +
                "(3) Das Arbeitsverhältnis endet, ohne dass es einer Kündigung bedarf, mit Ablauf des Monats, in dem " +
                "der Arbeitnehmer das gesetzlich geregelte Regelrentenalter erreicht. Bis zu diesem Termin kann das " +
                "Arbeitsverhältnis gemäß Absatz 1 von jeder Partei unter Einhaltung der dort geregelten " +
                "Kündigungsfristen gekündigt werden. Soweit sich die Regelaltersgrenze ändert, kommen die " +
                "geänderten gesetzlichen Vorschriften zur Anwendung. \n\n" +
                "(4) Das Arbeitsverhältnis endet ebenfalls zu dem Zeitpunkt, ab dem der Arbeitnehmer eine " +
                "unbefristete Rente wegen voller Erwerbsminderung erhält, frühestens aber an dem Tag, an dem der " +
                "entsprechende Rentenbescheid dem Arbeitnehmer zugeht und der Arbeitnehmer gegen diesen " +
                "Bescheid nicht Widerspruch einlegt. Legt der Arbeitnehmer Widerspruch gegen den Bescheid ein, so " +
                "endet das Arbeitsverhältnis, soweit rechtskräftig über die Gewährung der Erwerbsminderungsrente " +
                "entschieden ist. Der Arbeitnehmer verpflichtet sich, den Arbeitgeber über den Zugang eines " +
                "entsprechenden Rentenbescheides zu informieren. Während des Bezugs einer befristeten Rente " +
                "wegen voller Erwerbsminderung ruht das Arbeitsverhältnis. \n\n" +
                "(5) Der Arbeitnehmer kann während der Kündigungsfrist unter Fortzahlung der Vergütung und unter " +
                "Anrechnung auf Urlaubsansprüche sowie auf ein etwaiges Zeitguthaben aus dem Arbeitszeitkonto von " +
                "der Arbeitsverpflichtung freigestellt werden.\n\n" +
                "|<c>*§ 10 Allgemeine Pflichten*\n\n" +
                "|" +
                "(1) Der Arbeitnehmer ist verpflichtet, den Arbeitsanweisungen der zuständigen Vorgesetzten Folge zu " +
                "leisten. Er wird die ihm übertragenen Arbeiten sorgfältig und gewissenhaft ausführen. \n\n" +
                "(2) Der Arbeitnehmer darf Fahrzeuge auf dem Betriebsgelände nur in den angewiesenen Bereichen " +
                "und nur mit dessen ausdrücklicher Genehmigung abstellen. Das Abstellen geschieht in jedem Fall " +
                "ausschließlich auf Gefahr des Arbeitnehmers. \n\n" +
                "(3) Der Arbeitnehmer hat auf ein gepflegtes äußeres Erscheinungsbild zu achten, insbesondere die " +
                "berufs- oder betriebsübliche Kleidung zu tragen. Im Übrigen gilt die jeweils gültige Betriebsordnung.\n\n" +
                "|<c>*§ 11 Weitere Tätigkeiten, Wettbewerb*\n\n" +
                "|" +
                "(1) Während des Bestandes dieses Arbeitsvertrages ist dem Arbeitnehmer jegliche Tätigkeit für ein " +
                "Unternehmen untersagt, das mit dem Arbeitgeber im Wettbewerb steht. Das gleiche gilt für eine " +
                "Beteiligung an einem solchen Unternehmen, soweit diese nicht in einer reinen Kapitalanlage ohne " +
                "gesellschaftsrechtliche Einflussnahmemöglichkeit besteht. \n\n" +
                "(2) Eine anderweitige Erwerbstätigkeit ist ihm nur mit ausdrücklicher Zustimmung des Arbeitgebers " +
                "gestattet, wobei der Arbeitgeber diese Zustimmung erteilen wird, soweit berechtigte Belange des " +
                "Arbeitgebers nicht erheblich beeinträchtigt werden. Tritt eine solche Beeinträchtigung später auf, so " +
                "kann der Arbeitgeber die Zustimmung widerrufen.\n\n" +
                "|<c>*§ 12 Krankheit und Arbeitsverhinderung*\n\n" +
                "|" +
                "(1) Der Arbeitnehmer verpflichtet sich, jede krankheitsbedingte Arbeitsunfähigkeit und " +
                "Arbeitsverhinderung aus anderen Gründen sowie deren voraussichtliche Dauer dem Vorgesetzten " +
                "unverzüglich nach Erkennbarkeit, spätestens am ersten Tag der Abwesenheit zu Dienstbeginn, " +
                "telefonisch zu melden. Dauert die Arbeitsunfähigkeit bzw. Arbeitsverhinderung länger als ursprünglich " +
                "mitgeteilt, gelten die Pflichten entsprechend. \n\n" +
                "(2) Eine krankheitsbedingte Arbeitsunfähigkeit ist vom ersten Tag an durch ein ärztliches Attest zu " +
                "belegen. Das Gleiche gilt, wenn die Arbeitsunfähigkeit länger dauert als ursprünglich angegeben. \n\n" +
                "(3) Bei einer akut aufgetretenen Pflegesituation im Sinne von § 2 PflegeZG ist der Arbeitnehmer " +
                "verpflichtet, die Pflegebedürftigkeit des nahen Angehörigen und die Erforderlichkeit der Pflege durch " +
                "ein ärztliches Attest nachzuweisen. Das Attest muss spätestens am 3. Arbeitstag vorgelegt werden. \n\n" +
                "(4) Der Arbeitnehmer ist verpflichtet, einen Arbeitsunfall unverzüglich anzuzeigen. \n\n" +
                "(5) Der Arbeitnehmer ist bereit, sich im Falle von durch Tatsachen begründeten Zweifeln an seiner " +
                "Arbeitsfähigkeit oder an dem Bestand einer zur Arbeitsunfähigkeit führenden Erkrankung auf " +
                "Verlangen des Arbeitgebers einer vertrauensärztlichen Untersuchung zu unterziehen. \n\n" +
                "(6) Der Arbeitgeber ist zur Entgeltfortzahlung nur in den gesetzlich zwingend normierten Fällen " +
                "verpflichtet. Die Bestimmung des § 616 BGB wird – soweit rechtlich zulässig – ausgeschlossen. Dies " +
                "gilt insbesondere bei Arbeitsverhinderung wegen Kindpflege und Pflege nach dem Pflegezeitgesetz.\n\n" +
                "|<c>*§ 13 Abtretung von Schadensersatzforderungen*\n\n" +
                "|" +
                "Schadensersatzansprüche, die der Arbeitnehmer bei Unfall oder Krankheit gegen Dritte erwirkt, " +
                "werden hiermit an den Arbeitgeber bis zur Höhe der Beträge abgetreten, die der Arbeitgeber aufgrund " +
                "gesetzlicher, tariflicher oder vertraglicher Bestimmungen für die Dauer der Arbeitsunfähigkeit " +
                "gewähren muss. Dazu hat der Arbeitnehmer unverzüglich dem Arbeitgeber die zur Geltendmachung " +
                "der Schadenersatzansprüche erforderlichen Angaben zu machen.\n\n" +
                "|<c>*§ 14 Verpfändung des Arbeitseinkommens*\n\n" +
                "|" +
                "Der Arbeitnehmer darf seine Vergütungsansprüche an Dritte nur nach vorheriger schriftlicher " +
                "Zustimmung durch den Arbeitgeber verpfänden oder abtreten.\n\n" +
                "|<c>*§ 15 Fortbildung*\n\n" +
                "|" +
                "(1) Der Arbeitnehmer verpflichtet sich, die vom Arbeitgeber direkt oder indirekt gebotenen " +
                "Fortbildungsmöglichkeiten zu nutzen. Er ist bereit, auch Seminare und Schulungen außerhalb des " +
                "Betriebes und an anderen Orten zu besuchen, selbst wenn damit eine mehrtägige Ortsabwesenheit " +
                "verbunden ist. Dauert die Fortbildungsmaßnahme, einschließlich eventueller, vom Arbeitnehmer selbst " +
                "organisierter Wegezeiten, länger als die vertraglich abzuleistende Arbeitszeit, so zählt die darüber " +
                "hinausgehende Zeit nicht als Mehrarbeit. \n\n" +
                "(2) Die Teilnahme an Schulungen und Seminaren ist generell durch die Vergütung nach § 4 " +
                "umfassend abgegolten.\n" +
                "\n" +
                "|<c>*§ 16 Gesetzlicher Urlaub*\n\n" +
                "|" +
                "(1) Die Dauer des Urlaubs und die Bezahlung richten sich nach dem Bundesurlaubsgesetz. Der " +
                "Anspruch auf Urlaub vermindert sich – soweit gesetzlich zulässig – zeitanteilig für Tage, an denen " +
                "weder Arbeitspflicht noch Entgeltanspruch bestehen; dies gilt insbesondere bei unbezahltem " +
                "Sonderurlaub und Kurzarbeit, nicht aber im Falle einer Erkrankung des Arbeitnehmers. \n\n" +
                "(2) Der Urlaub wird im Rahmen der betrieblichen Möglichkeiten und unter Berücksichtigung der " +
                "persönlichen Wünsche des Arbeitnehmers gewährt.\n\n" +
                "|<c>*§ 16a Freiwillig gewährter Urlaub*\n\n" +
                "|" +
                "(1) Gewährt das Unternehmen über den gesetzlichen Urlaub hinaus zusätzlichen Urlaub, handelt es " +
                "sich um eine freiwillige Leistung, auf die auch nach wiederholter Gewährung kein Rechtsanspruch " +
                "entsteht. Auch das Entstehen einer betrieblichen Übung wird ausdrücklich ausgeschlossen. \n\n" +
                "(2) Gewährt das Unternehmen auf Basis der Freiwilligkeit generell zusätzlichen Urlaub, haben nur " +
                "diejenigen Arbeitnehmer Anspruch auf freiwilligen Urlaub, die im Urlaubsjahr ganzjährig in einem " +
                "Arbeitsverhältnis gestanden haben. Im Ein- und Austrittsjahr besteht kein Anspruch auf freiwilligen " +
                "Urlaub. § 16 Abs. 1 S. 2 dieses Vertrages gilt auch für den freiwilligen Urlaub entsprechend. \n\n" +
                "(3) Der genommene Urlaub wird zunächst auf den gesetzlichen und sodann auf den etwa freiwillig " +
                "gewährten Urlaub angerechnet. \n\n" +
                "(4) Nicht in Anspruch genommener freiwillig gewährter Urlaub verfällt grundsätzlich mit Ablauf des " +
                "31.03. des Folgejahres, ohne dass es eines ausdrücklichen Hinweises des Arbeitsgebers im Einzelfall " +
                "bedarf. Dies gilt auch bei einer langandauernden Erkrankung. Für bei Ausscheiden aus dem " +
                "Arbeitsverhältnis noch bestehenden freiwillig gewährten Urlaub erfolgt keine Abgeltung oder ein " +
                "sonstiger Ausgleich.\n\n" +
                "|<c>*§ 17 Vertraulichkeit*\n\n" +
                "|" +
                "(1) Der Arbeitnehmer ist verpflichtet, insbesondere auch während der Zeit nach Beendigung dieses " +
                "Arbeitsvertrages alle vertraulichen Angelegenheiten, Betriebs- und Geschäftsgeheimnisse des " +
                "Arbeitgebers und verbundener Unternehmen, welche ihm bei Ausübung seiner Tätigkeiten für den " +
                "Arbeitgeber zur Kenntnis gelangen (insbesondere Verfahren, Daten, Know-how, Marketing-Pläne, " +
                "Geschäftsplanungen, Budgets, Lizenzen, Preise, Kosten und Kunden- und Lieferantenlisten) oder die " +
                "vom Arbeitgeber als vertraulich bezeichnet werden, streng geheim zu halten. Ausgenommen von der " +
                "Verschwiegenheitsverpflichtung sind Angaben gegenüber Behörden oder aufgrund gesetzlicher " +
                "Verpflichtung, soweit diese erforderlich sind. \n\n" +
                "(2) Der Arbeitnehmer sichert zu, dass er insbesondere sämtliche ihm in Ausübung des " +
                "Arbeitsverhältnisses übergebenen oder bekannt gewordenen Daten und Dokumente über die " +
                "Angelegenheiten des Unternehmens, seiner Mitarbeiter, Lieferanten, Kunden und sonstigen Kontakte " +
                "zeitlich unbegrenzt, insbesondere auch über die Dauer des Vertragsverhältnisses hinaus, streng " +
                "vertraulich behandelt und geheim hält. Er versichert, dass er derartige Daten und Dokumente Dritten " +
                "nicht zugänglich machen oder sonst zum eigenen oder fremden Nutzen preisgeben wird, außer in " +
                "Erfüllung seiner vertraglichen Pflichten. \n\n" +
                "(3) In besonderer Weise sind Daten von Kunden vertraulich zu behandeln und vor dem Zugriff Dritter " +
                "zu schützen. Auch eine Kommunikation mit Kunden über soziale Netzwerke oder unverschlüsselte " +
                "Messenger-Dienste, insbesondere WhatsApp, ist untersagt. \n\n" +
                "(4) *Alle Angaben, die dieses Arbeitsverhältnis betreffen, sind vertraulich zu behandeln und* " +
                "*dürfen Dritten nicht zugänglich gemacht werden.*\n\n" +
                "|<c>*§ 18 Zuverlässigkeit*\n\n" +
                "|" +
                "(1) Der Arbeitnehmer versichert, dass keine einschlägigen Vorstrafen vorliegen, die Zweifel an seiner " +
                "beruflichen Gewissenhaftigkeit, Zuverlässigkeit und seinem Verantwortungsgefühl begründen können.\n" +
                "\n" +
                "(2) Ein aktueller Eintrag im Führungszeugnis, der Zweifel an der Eignung und/oder Zuverlässigkeit des " +
                "Arbeitnehmers begründet, kann Grund für arbeitsrechtliche Konsequenzen bis hin zu einer " +
                "ordentlichen oder außerdordentlichen Kündigung bzw. Anfechtung des Arbeitsvertrages sein.\n\n" +
                "|<c>*§ 19 Rückgabe von Firmenunterlagen*\n\n" +
                "|" +
                "Beim Ausscheiden des Arbeitnehmers aus dem Betrieb sind alle dem Arbeitgeber gehörenden " +
                "Unterlagen, schriftliche und digitale Aufzeichnungen, Werkzeuge, Zugangschips bzw. -karten, " +
                "Schlüssel etc. herauszugeben. Wird der Arbeitnehmer während des Arbeitsverhältnisses freigestellt, " +
                "so kann der Arbeitgeber die Herausgabe bereits ab Beginn der Freistellung verlangen. Dem " +
                "Arbeitgeber steht ein Zurückbehaltungsrecht an sämtlichen Leistungen bis zur Erfüllung fälliger " +
                "Herausgabeansprüche zu.\n\n" +
                "|<c>*§ 20 Schriftform, Rechtsgültigkeit*\n\n" +
                "|" +
                "(1) Änderungen und Ergänzungen dieses Vertrages bedürfen, soweit sie nicht auf einer individuellen " +
                "Abrede beruhen, der Schriftform. Gleiches gilt auch für die Aufhebung dieses " +
                "Schriftformerfordernisses. Den Parteien ist bewusst, dass ein Erwachsen von Ansprüchen aus einer " +
                "betrieblichen Übung daher ausgeschlossen ist. \n\n" +
                "(2) Für diesen Arbeitsvertrag gilt ausschließlich deutsches Recht als vereinbart\n\n" +
                "|<c>*§ 21 Verfallklausel*\n\n" +
                "|" +
                "(1) Alle wechselseitigen Ansprüche aus dem Arbeitsvertrag und solche, die damit in Verbindung " +
                "stehen sowie Ansprüche aus Anlass der Beendigung des Arbeitsverhältnisses verfallen, wenn sie nicht " +
                "innerhalb von drei Monaten nach *Fälligkeit* gegenüber der anderen Vertragspartei in Textform geltend " +
                "gemacht worden sind. War die Fälligkeit des Anspruches für den Arbeitnehmer auch bei größter " +
                "Sorgfalt nicht erkennbar, so können Ansprüche abweichend noch innerhalb von drei Monaten nach " +
                "dem Zeitpunkt geltend gemacht werden, an dem der Arbeitnehmer Kenntnis von der Fälligkeit haben " +
                "musste. \n\n" +
                "(2) Lehnt die andere Vertragspartei den rechtzeitig geltend gemachten Anspruch ab oder erklärt sie " +
                "sich nicht innerhalb einer Erklärungsfrist von vier Wochen nach der Geltendmachung des Anspruchs, " +
                "so verfällt dieser dennoch, wenn er nicht innerhalb einer Frist von drei Monaten nach Ablehnung oder " +
                "dem Ablauf der Erklärungsfrist gerichtlich geltend gemacht wird. \n\n" +
                "(3) Die Ausschluss- und Verfallfristen gelten nicht für wechselseitige Ansprüche auf Schadensersatz " +
                "wegen unerlaubter Handlung sowie für Ansprüche auf verbindliche Mindestlöhne. Sie gelten ebenfalls " +
                "nicht für wechselseitige Ansprüche auf Erstattung von Lohn- und Kirchensteuer, Solidaritätszuschlag " +
                "sowie Sozialversicherungsbeiträgen, die durch Nachberechnung entstanden sind.\n\n" +
                "|<c>*§ 22 – entfällt –*\n\n" +
                "|" +
                "|<c>*§ 23 – entfällt –*\n\n" +
                "|" +
                "|<c>*§ 24 Personalvollmachten*\n\n" +
                "|" +
                "Dem Arbeitnehmer ist bekannt, dass jeder Geschäftsführer und jeder Prokurist des Arbeitgebers und " +
                "seines Komplementärs einzeln und unabhängig von der Reichweite seines Vertretungsrechts im " +
                "Allgemeinen sowie die weiteren Mitglieder der Geschäftsleitung jeweils einzeln durch den Arbeitgeber " +
                "bevollmächtigt sind, alle Rechtshandlungen betreffend das Arbeitsverhältnis für den Arbeitgeber " +
                "vorzunehmen. Diese Personalvollmacht erstreckt sich insbesondere auf Einstellungen sowie den " +
                "Ausspruch und die Entgegennahme von Kündigungen. Der Arbeitnehmer wird laufend über " +
                "betriebliche Aushänge über die Personen informiert, die als weitere Mitglieder der Geschäftsleitung " +
                "gelten; Geschäftsführer und Prokuristen werden durch das Handelsregister allgemein bekannt " +
                "gemacht.\n\n" +
                "|<c>*§ 25 Sondervereinbarung*\n\n" +
                "|" +
                "(1) Als Eintrittsdatum gilt der [startDate]. \n\n" +
                "(2) Das Unternehmen gewährt zur Zeit als freiwillige Leistung [?] Werktage Erholungsurlaub gemäß" +
                "§ 16a dieses Vertrages.\n\n" +
                "|<0>*Optional Sonderleistungen:*\n" +
                "|<30>Der Arbeitnehmer erhält zusätzlich zu der Vergütung nach § 4 folgende Leistungszulagen /\n" +
                "Pämien:\n" +
                "[?]\n\n" +
                "|<0>*Optional Kündigungsfrist:*\n" +
                "|<30>#Nach erfolgreich absolvierter Probezeit gilt eine beiderseitige Kündigungsfrist von [noticePeriod] Monaten " +
                "zum Monatsende als vereinbart.\n" +
                "|<30>#Die Kündigungsfrist beträgt für beide Seiten [noticePeriod] Monate zum Monatsende.\n\n" +
                "|<0>*Optional im Falle einer Ablösung eines bestehenden Vertrages (gleicher Arbeitgeber):*\n" +
                "|<30>Durch diesen Vertrag werden alle bisherigen mündlichen und schriftlichen Vereinbarungen " +
                "einschließlich etwaiger betrieblicher Übungen und nachwirkender Betriebsvereinbarungen " +
                "insgesamt abgelöst und damit gegenstandslos; dies gilt nicht für die unmittelbar und zwingend " +
                "geltenden Betriebsvereinbarungen und gültige Regelungsabreden. Die Parteien wollen die " +
                "Bedingungen des Arbeitsverhältnisses insgesamt neu festlegen.\n\n" +
                "|<0>*Optional im Falle einer Übernahme von anderem Arbeitgeber aus der Gruppe:*\n" +
                "|<30>Mit Abschluss dieses Vertrages heben die Parteien zugleich das bisher bestehende " +
                "Arbeitsverhältnis zwischen dem Arbeitnehmer und der [?] zum Stichtag des Beginn des " +
                "Arbeitsverhältnisses nach diesem Vertrag auf. Der Arbeitgeber handelt insoweit zugleich in " +
                "Vollmacht für den bisherigen Arbeitgeber. Die Parteien stellen klar, dass sich das Arbeitsverhältnis " +
                "ausschließlich nach den Bestimmungen dieses Vertrages richtet. Ein Besitzstand aus dem " +
                "bisherigen Arbeitsverhältnis wird nur und nur insoweit anerkannt, wenn und soweit dies in diesem " +
                "Vertrag ausdrücklich zugestanden wird.\n\n" +
                "|<0>*Optional VwL:*\n" +
                "|<30>Das Unternehmen gewährt auf Basis der Freiwilligkeit im Sinne von § 5 dieses Vertrages nach " +
                "Vollendung des ersten Jahres Betriebszugehörigkeit nach Eintritt [capitalFormingPayments] EUR vermögenswirksame " +
                "Leistungen.\n\n" +
                "|<0>*Optional Leitender Angestellter:*\n" +
                "|<30>Der Arbeitnehmer ist nach übereinstimmender Auffassung der Vertragspartner als leitender " +
                "Angestellter im Sinne des § 5 Abs. 3 BetrVG einzustufen.\n\n" +
                "|<0>*Optional Handynutzung:*\n" +
                "|<30>Während der Arbeitszeit ist die Nutzung von privaten Handys zu privaten Zwecken untersagt.\n\n" +
                "|<0>*Optional Arbeitszeit:* \n" +
                "|<30>#Dem Arbeitnehmer ist bekannt, dass die Arbeit im Schicht- und Wechselschichtdienst im " +
                "Zeitraum von 04:30 Uhr bis 21:00 Uhr zu leisten ist. \n" +
                "|<30>#In der Regel beträgt die Arbeitszeit täglich (Montag-Freitag) 5 Stunden. Die " +
                "Monatsarbeitszeit beträgt mindestens 80 Stunden.\n\n" +
                "|<0>*Optional bei Werkstudenten*\n" +
                "|<30>Um den Status als Werkstudent zu erfüllen, wird vereinbart, dass der Werkstudent während der " +
                "Studienzeit maximal 20 Stunden pro Woche arbeitet. Diese Arbeitszeit kann auf bis zu 40 Stunden " +
                "pro Woche in den Semesterferien erhöht werden (hierzu Bedarf es einer Bescheinigung über die " +
                "Dauer der Semsterferien).\n\n" +
                "|<0>*Optional bei Anlagen:*\n" +
                "|<30>#Die Anlage zum Arbeitsvertrag [id] ist fester Bestandteil dieses Vertrages.\n" +
                "|<30>#Die gesondert abgeschlossene Vergütungsvereinbarung ist fester Bestandteil dieses " +
                "Vertrages und ergänzt dessen Bestimmungen.\n\n\n");
        contract.setLocation("Am Rondell 1\n" +
                "12529 Schönefeld");
        contract.setId(7L);
        contract.setDateOfSignature(LocalDate.now());
        Signee signee = new Signee();
        signee.setName("Friedrich Maier");
        signee.setPosition("Geschäftsleitung");
        List<Signee> signeeList = new ArrayList<>();
        signeeList.add(signee);
        contract.setSigneeList(signeeList);
        contract.setFileName("LayoutTest.pdf");
        contract.setCandidateId(candidate.getCandidateId());
        contract.setPlaceOfSignature("Schönefeld");
        contract.setContractVersionName(ContractVersionName.VERWALTUNG);
        contract.setVersionTemplateName(VersionTemplateName.KEIN_TEMPLATE);
        contract.setCreator("Krause");
        pdfLayoutService.createPdf(contract);
        final OutputStream outputStream = new FileOutputStream("LayoutTest.pdf");
        pdfLayoutService.createPdf(contract).save(outputStream);
        System.out.println(System.getProperty("user.dir"));
    }
}
