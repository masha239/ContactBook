
import java.io.*;
import java.time.temporal.ChronoUnit;
import java.util.regex.*;
import java.util.*;
import java.time.*;

public class Main {
    public static void main(String[] args) throws IOException {
        ContactList book;
        try {
            FileInputStream fis = new FileInputStream("PhoneBook.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            book = (ContactList) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            Scanner scanner = new Scanner(System.in);
            book = new ContactList(scanner);
        }
        book.menu();
        FileOutputStream fos = new FileOutputStream("PhoneBook.txt");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(book);
        oos.close();
        fos.close();
    }
}

abstract class Contact implements Serializable {
    protected String name;
    protected String number;
    protected LocalDateTime created;
    protected LocalDateTime lastEdit;
    protected transient Scanner scanner;

    private void readObject(ObjectInputStream ois) throws Exception {
        ois.defaultReadObject();
        scanner = new Scanner(System.in);
    }

    public LocalDateTime getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(LocalDateTime lastEdit) {
        this.lastEdit = lastEdit;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Contact(Scanner scanner) {
        this.scanner = scanner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        if (tryNumber(number)) {
            this.number = number;
        } else {
            this.number = "";
            System.out.println("Wrong number format!");
        }
    }

    private boolean tryNumber(String number) {
        String s1 = "\\([0-9a-zA-Z]+\\)";
        String s2 = "[0-9a-zA-Z]+";
        String s3 = "[0-9a-zA-Z]+[ -]\\([0-9a-zA-Z]{2,}\\)";
        Pattern pattern = Pattern.compile("\\+?" + "(" + s1 + "|" + s2 + "|" + s3 + ")" + "([ -]([0-9a-zA-Z]{2,}[ -]?))*");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    abstract public void edit();

    abstract public boolean info();

    abstract public void print();

    abstract public boolean contains(String s);


}

class ContactPerson extends Contact implements Serializable {
    private LocalDate birthDate;
    private String gender;
    private String surname;

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public ContactPerson(Scanner scanner) {
        super(scanner);
    }

    public void setBirthDate(String birthDate) {
        try {
            this.birthDate = LocalDate.parse(birthDate);
        } catch (Exception DateTimeParseException) {
            System.out.println("Bad birth date!");
            this.birthDate = null;
        }
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (gender.equals("F") || gender.equals("M")) {
            this.gender = gender;
        } else {
            System.out.println("Bad gender!");
            this.gender = "";
        }
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void edit() {
        System.out.println("Select a field (name, surname, birth, gender, number): ");
        String field = scanner.nextLine();
        boolean edited = true;
        if (field.equals("number")) {
            System.out.println("Enter number: ");
            String number = scanner.nextLine();
            setNumber(number);
        } else if (field.equals("name")) {
            System.out.println("Enter name: ");
            String name = scanner.nextLine();
            setName(name);
        } else if (field.equals("surname")) {
            System.out.println("Enter surname: ");
            String surname = scanner.nextLine();
            setSurname(surname);
        } else if (field.equals("gender")) {
            System.out.println("Enter gender: ");
            String gender = scanner.nextLine();
            setGender(gender);
        } else if (field.equals("birth")) {
            System.out.println("Enter birthdate: ");
            String birth = scanner.nextLine();
            setSurname(birth);
        } else {
            edited = false;
            System.out.println("Wrong field!");
        }
        if (edited) {
            setLastEdit(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            System.out.println("Saved");
        }
        info();
    }

    ;

    public boolean info() {
        System.out.println("Name: " + this.getName());
        System.out.println("Surname: " + this.getSurname());
        System.out.println("Birth date: " + ((this.getBirthDate() == null) ? "[no data]" : this.getBirthDate().toString()));
        System.out.println("Gender: " + ((this.gender.equals("")) ? "[no data]" : this.getGender()));
        System.out.println("Number: " + ((this.number.equals("")) ? "[no data]" : this.getNumber()));
        System.out.println("Time created: " + this.getCreated().toString());
        System.out.println("Time last edit: " + this.getLastEdit().toString());
        System.out.println();
        System.out.println("[record] Enter action (edit, delete, menu): ");
        String s = scanner.nextLine();
        if (s.equals("edit")) {
            edit();
        } else if (s.equals("delete")) {
            System.out.println("The record is deleted!");
            return true;
        } else if (!s.equals("menu")) {
            System.out.println("You want something strange, but you'll get this:");
        }
        return false;
    }

    ;

    public void print() {
        System.out.println(this.getName() + " " + this.getSurname());
    }

    public boolean contains(String s) {
        if (this.number.toUpperCase().matches(s)) return true;
        if (this.name.toUpperCase().matches(s)) return true;
        if (this.surname.toUpperCase().matches(s)) return true;
        return false;
    }
}

class ContactOrganization extends Contact implements Serializable {
    private String address;

    public ContactOrganization(Scanner scanner) {
        super(scanner);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void edit() {
        System.out.println("Select a field (name, address, number): ");
        String field = scanner.nextLine();
        boolean edited = true;
        if (field.equals("number")) {
            System.out.println("Enter number: ");
            String number = scanner.nextLine();
            setNumber(number);
        } else if (field.equals("name")) {
            System.out.println("Enter name: ");
            String name = scanner.nextLine();
            setName(name);
        } else if (field.equals("address")) {
            System.out.println("Enter address: ");
            String adress = scanner.nextLine();
            setAddress(adress);
        } else {
            edited = false;
            System.out.println("Wrong field!");
        }
        if (edited) {
            setLastEdit(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            System.out.println("Saved");
        }
        info();
    }

    public boolean info() {
        System.out.println("Organization name: " + this.getName());
        System.out.println("Address: " + this.getAddress());
        System.out.println("Number: " + ((this.number.equals("")) ? "[no data]" : this.getNumber()));
        System.out.println("Time created: " + this.getCreated().toString());
        System.out.println("Time last edit: " + this.getLastEdit().toString());
        System.out.println();
        System.out.println("[record] Enter action (edit, delete, menu): ");
        String s = scanner.nextLine();
        if (s.equals("edit")) {
            edit();
        } else if (s.equals("delete")) {
            System.out.println("The record is deleted!");
            return true;
        } else if (!s.equals("menu")) {
            System.out.println("You want something strange, but you'll get this:");
        }
        return false;
    }

    ;

    public void print() {
        System.out.println(this.getName());
    }

    public boolean contains(String s) {
        if (this.number.toUpperCase().matches(s)) return true;
        if (this.name.toUpperCase().matches(s)) return true;
        if (this.address.toUpperCase().matches(s)) return true;
        return false;
    }
}

class ContactFactory {
    private Scanner scanner;

    public ContactFactory(Scanner scanner) {
        this.scanner = scanner;
    }

    Contact createContactPerson() {
        ContactPerson person = new ContactPerson(scanner);
        System.out.println("Enter the name: ");
        String name = scanner.nextLine();
        person.setName(name);
        System.out.println("Enter the surname: ");
        String surname = scanner.nextLine();
        person.setSurname(surname);
        System.out.println("Enter the birth date: ");
        String birthDate = scanner.nextLine();
        person.setBirthDate(birthDate);
        System.out.println("Enter the gender: ");
        String gender = scanner.nextLine();
        person.setGender(gender);
        System.out.println("Enter the number: ");
        String number = scanner.nextLine();
        person.setNumber(number);
        person.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        person.setLastEdit(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return person;
    }

    Contact createContactOrganization() {
        ContactOrganization organization = new ContactOrganization(scanner);
        System.out.println("Enter the organization name: ");
        String name = scanner.nextLine();
        organization.setName(name);
        System.out.println("Enter the address: ");
        String address = scanner.nextLine();
        organization.setAddress(address);
        System.out.println("Enter the number: ");
        String number = scanner.nextLine();
        organization.setNumber(number);
        organization.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        organization.setLastEdit(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return organization;
    }
}

class ContactList implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient ContactFactory factory;
    private ArrayList<Contact> myList;
    private transient Scanner scanner;

    private void readObject(ObjectInputStream ois) throws Exception {
        ois.defaultReadObject();
        scanner = new Scanner(System.in);
        factory = new ContactFactory(scanner);
    }

    public ContactList(Scanner scanner) {
        this.scanner = scanner;
        this.myList = new ArrayList<Contact>();
        this.factory = new ContactFactory(scanner);
    }

    public void menu() {
        System.out.println("[menu] Enter action (add, list, search, count, exit): ");
        String answer = scanner.nextLine();
        while (!answer.equals("exit")) {
            if (answer.equals("count")) count();
            else if (answer.equals("add")) add();
            else if (answer.equals("search")) search();
            else if (answer.equals("list")) list();
            else System.out.println("You want something strange, but you'll get this:");
            System.out.println();
            System.out.println("[menu] Enter action (add, list, search, count, exit): ");
            answer = scanner.nextLine();
        }
    }

    private void count() {
        System.out.println("The Phone Book has " + this.myList.size() + " records.");
    }

    private void add() {
        System.out.println("Enter the type (person, organization): ");
        String whoItIs = scanner.nextLine();
        if (whoItIs.equals("person")) {
            myList.add(factory.createContactPerson());
            System.out.println("The record added.");
        } else if (whoItIs.equals("organization")) {
            myList.add(factory.createContactOrganization());
            System.out.println("The record added.");
        } else {
            System.out.println("Wrong type, try again!");
        }
    }

    public void list() {
        for (int i = 0; i < myList.size(); i++) {
            System.out.print((i + 1) + ". ");
            myList.get(i).print();
        }
        System.out.println();
        System.out.println("[list] Enter action ([number], back): ");
        String s = scanner.nextLine();
        if (!s.equals("back")) {
            try {
                int number = Integer.parseInt(s) - 1;
                boolean isDeleted = myList.get(number).info();
                if (isDeleted) myList.remove(number);
            } catch (Exception e) {
                System.out.println("Wrong number!");
            }
        }
    }

    public void search() {
        System.out.println("Enter search query: ");
        String s = scanner.nextLine();
        String searctText = (".*" + s + ".*").toUpperCase();
        ArrayList<Contact> searchList = new ArrayList<>();
        for (int i = 0; i < myList.size(); i++)
            if (myList.get(i).contains(searctText)) {
                searchList.add(myList.get(i));
            }
        System.out.println("Found " + searchList.size() + " results:");
        for (int i = 0; i < searchList.size(); i++) {
            System.out.print((i + 1) + ". ");
            searchList.get(i).print();
        }
        System.out.println();
        System.out.println("[search] Enter action ([number], back, again): ");
        s = scanner.nextLine();
        if (s.equals("again")) {
            search();
        } else if (!s.equals("back")) {
            try {
                int number = Integer.parseInt(s) - 1;
                boolean isDeleted = searchList.get(number).info();
                if (isDeleted) myList.remove(searchList.get(number));
            } catch (Exception e) {
                System.out.println("Wrong number!");
            }
        }
    }
}

