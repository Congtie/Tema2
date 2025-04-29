package app;

import java.util.*;

// 1. Gestionarea permisiunilor cu interfete marker si verificare prin instanceof
interface PoateEdita {}
interface PoateSterge {}
interface PoateVizualiza {}

class Utilizator {}
class Administrator extends Utilizator implements PoateEdita, PoateSterge, PoateVizualiza {}
class Editor extends Utilizator implements PoateEdita, PoateVizualiza {}
class Vizitator extends Utilizator implements PoateVizualiza {}

class ActiuneService {
    public static void arataActiuni(Utilizator u) {
        System.out.println("Utilizator: " + u.getClass().getSimpleName());
        if (u instanceof PoateVizualiza) System.out.println("- Poate Vizualiza");
        if (u instanceof PoateEdita)    System.out.println("- Poate Edita");
        if (u instanceof PoateSterge)   System.out.println("- Poate Sterge");
        System.out.println();
    }
}

// 2. Utilizarea claselor sealed pentru validarea tipurilor intr-un sistem de plati
sealed interface MetodaPlata permits Card, Cash, TransferBancar {}

final class Card implements MetodaPlata {
    private final String cvv;
    private final String dataExpirare;
    public Card(String cvv, String dataExpirare) {
        this.cvv = cvv;
        this.dataExpirare = dataExpirare;
    }
    public boolean validate() {
        return cvv.matches("\\d{3}") && dataExpirare.matches("\\d{2}/\\d{2}");
    }
}

final class Cash implements MetodaPlata {
    public void process() {
        System.out.println("Tranzactie cash: instanta.");
    }
}

final class TransferBancar implements MetodaPlata {
    private final String iban;
    public TransferBancar(String iban) { this.iban = iban; }
    public boolean validate() {
        return iban.matches("[A-Z]{2}\\d{2}[A-Z0-9]{1,30}");
    }
}

class PlataService {
    public static void valideazaMetoda(MetodaPlata m) {
        System.out.println("Metoda: " + m.getClass().getSimpleName());
        if (m instanceof Card c) {
            System.out.println("CVV si data expirare valide? " + c.validate());
        } else if (m instanceof Cash cash) {
            cash.process();
        } else if (m instanceof TransferBancar tb) {
            System.out.println("IBAN valid? " + tb.validate());
        }
        System.out.println();
    }
}

// 3. Gestionarea exceptiilor custom si tratarea erorilor multiple
class RezervareException extends Exception {}
class LocIndisponibilException extends RezervareException {}
class DateInvalideException extends RezervareException {}

class RezervareService {
    public void rezervaLoc(String data, int loc) throws RezervareException {
        if (!data.matches("\\d{4}-\\d{2}-\\d{2}")) throw new DateInvalideException();
        if (loc < 1 || loc > 100) throw new LocIndisponibilException();
        System.out.println("Loc rezervat pentru " + data + ", la numarul " + loc);
    }
}

// 4. Implementarea unei clase adaptor pentru integrarea cu o biblioteca terta
class SistemExistent {
    public void afiseazaXML(String xml) {
        System.out.println("SistemExistent afiseaza XML:\n" + xml);
    }
}

class GenerareJSON {
    public String genereazaJSON() {
        return "{\"mesaj\": \"Salut lume\"}";
    }
}

class AdaptorJsonToXml {
    private final SistemExistent sistem;
    public AdaptorJsonToXml(SistemExistent sistem) {
        this.sistem = sistem;
    }
    public void afiseaza(String json) {
        // Simpla transformare JSON->XML
        String xml = "<root><mesaj>" + json.replaceAll(".*\"mesaj\": \"(.*)\".*", "$1") + "</mesaj></root>";
        sistem.afiseazaXML(xml);
    }
}

// 5. Mostenire controlata si abstractizare avansata cu clase finale si protected
abstract class OrganismViu {
    public abstract void respira();
    public abstract void seHraneste();
}

abstract class Animal extends OrganismViu {
    @Override public final void respira() {
        System.out.println(getClass().getSimpleName() + " respira aer.");
    }
}

abstract class Mamifer extends Animal {
    protected void arePar() {
        System.out.println(getClass().getSimpleName() + " are par.");
    }
}

class Urs extends Mamifer {
    @Override public void seHraneste() {
        System.out.println("Ursul se hraneste cu miere.");
    }
    public void grizzly() { /* comportament special */ }
}

class Delfin extends Mamifer {
    @Override public void seHraneste() {
        System.out.println("Delfinul se hraneste cu pesti.");
    }
}

// 6. Interfete extinse si metode default/statice/private
interface Dispozitiv {
    void porneste();
    void opreste();
    default void status() {
        afiseazaStareInterna();
        System.out.println("Stare generala OK.");
    }
    static void descriereGenerala() {
        System.out.println("Dispozitiv generic cu operatii de baza.");
    }
    private void afiseazaStareInterna() {
        System.out.println("[Debug] Verificare interna de stare...");
    }
}

interface Smart extends Dispozitiv {
    void conecteazaLaInternet();
}

interface Conectabil extends Dispozitiv {
    void conecteaza();
}

class Telefon implements Smart, Conectabil {
    public void porneste() { System.out.println("Telefon pornit."); }
    public void opreste() { System.out.println("Telefon oprit."); }
    public void conecteazaLaInternet() { System.out.println("Telefon conectat la internet."); }
    public void conecteaza() { System.out.println("Telefon conectat la un alt dispozitiv."); }
}

class Smartwatch implements Smart {
    public void porneste() { System.out.println("Smartwatch pornit."); }
    public void opreste() { System.out.println("Smartwatch oprit."); }
    public void conecteazaLaInternet() { System.out.println("Smartwatch conectat la internet."); }
}

class Televizor implements Conectabil {
    public void porneste() { System.out.println("Televizor pornit."); }
    public void opreste() { System.out.println("Televizor oprit."); }
    public void conecteaza() { System.out.println("Televizor conectat la Wi-Fi."); }
}

// 7. Gestionarea unei colectii cu HashSet, HashMap si suprascrierea metodelor equals() si hashCode()
class Produs {
    private final String cod;
    private final String nume;
    private final double pret;
    public Produs(String cod, String nume, double pret) {
        this.cod = cod; this.nume = nume; this.pret = pret;
    }
    public String getCod() { return cod; }
    @Override public boolean equals(Object o) {
        return this == o || (o instanceof Produs p && cod.equals(p.cod));
    }
    @Override public int hashCode() {
        return Objects.hash(cod);
    }
    @Override public String toString() {
        return nume + " (" + cod + "): " + pret;
    }
}

// 8. Enum-uri avansate cu metode si constructori personalizati
enum NivelAcces {
    ADMIN(1, "Acces complet"),
    EDITOR(2, "Acces de editare"),
    USER(3, "Acces standard"),
    GUEST(4, "Acces limitat");

    private final int cod;
    private final String descriere;
    NivelAcces(int cod, String descriere) {
        this.cod = cod; this.descriere = descriere;
    }
    public int getCod() { return cod; }
    public String getDescriere() { return descriere; }
    public static NivelAcces dinCod(int cod) {
        for (NivelAcces n : values()) if (n.cod == cod) return n;
        throw new IllegalArgumentException("Cod invalid: " + cod);
    }
}

class ContUtilizator {
    private final NivelAcces nivel;
    public ContUtilizator(NivelAcces nivel) { this.nivel = nivel; }
    public NivelAcces getNivel() { return nivel; }
    @Override public String toString() { return "Cont cu nivel: " + nivel; }
}

// Clasa Main pentru demonstratie
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Permisiuni Utilizatori ===");
        ActiuneService.arataActiuni(new Administrator());
        ActiuneService.arataActiuni(new Editor());
        ActiuneService.arataActiuni(new Vizitator());

        System.out.println("=== Validare Metode Plata ===");
        PlataService.valideazaMetoda(new Card("123", "12/24"));
        PlataService.valideazaMetoda(new Cash());
        PlataService.valideazaMetoda(new TransferBancar("RO49AAAA1B31007593840000"));

        System.out.println("=== Rezervari ===");
        RezervareService rs = new RezervareService();
        try {
            rs.rezervaLoc("2025-05-01", 10);
        } catch (DateInvalideException e) {
            System.err.println("Data invalida!");
        } catch (LocIndisponibilException e) {
            System.err.println("Loc indisponibil!");
        } catch (RezervareException e) {
            System.err.println("Eroare rezervare: " + e);
        } finally {
            System.out.println("Raport final rezervare.");
        }

        System.out.println("=== Adaptor JSON->XML ===");
        SistemExistent ext = new SistemExistent();
        GenerareJSON gen = new GenerareJSON();
        AdaptorJsonToXml adaptor = new AdaptorJsonToXml(ext);
        adaptor.afiseaza(gen.genereazaJSON());

        System.out.println("=== Organisme Vii ===");
        List<OrganismViu> lista = List.of(new Urs(), new Delfin());
        lista.forEach(o -> { o.respira(); o.seHraneste(); });

        System.out.println("=== Dispozitive ===");
        Dispozitiv.descriereGenerala();
        Telefon t = new Telefon();
        t.porneste(); t.status(); t.conecteazaLaInternet(); t.opreste();

        System.out.println("=== Colectii Produse ===");
        Set<Produs> set = new HashSet<>();
        Produs p1 = new Produs("A1", "Produs1", 10.0);
        Produs p2 = new Produs("A1", "AltNume", 12.0);
        set.add(p1); set.add(p2);
        set.forEach(System.out::println);
        Map<Produs, Integer> magazin = new HashMap<>();
        magazin.put(p1, 5);
        magazin.forEach((prod, stoc) -> System.out.println(prod + " -> stoc: " + stoc));

        System.out.println("=== Conturi Utilizatori ===");
        List<ContUtilizator> conturi = List.of(
                new ContUtilizator(NivelAcces.ADMIN),
                new ContUtilizator(NivelAcces.USER),
                new ContUtilizator(NivelAcces.GUEST)
        );
        conturi.stream()
                .filter(c -> c.getNivel().ordinal() < NivelAcces.USER.ordinal())
                .forEach(System.out::println);
    }
}