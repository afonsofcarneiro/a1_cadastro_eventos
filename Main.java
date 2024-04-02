import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// Model Classes
class User {
    private String name;
    private String city;
    private String email;

    public User(String name, String city, String email) {
        this.name = name;
        this.city = city;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }
}

class Event {
    private String name;
    private String address;
    private String category;
    private Date dateTime;
    private String description;

    public Event(String name, String address, String category, Date dateTime, String description) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.dateTime = dateTime;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCategory() {
        return category;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return name + ";" + address + ";" + category + ";" + dateFormat.format(dateTime) + ";" + description;
    }
}

// Controller Classes
class UserController {
    private List<User> users = new ArrayList<>();

    public void registerUser(String name, String city, String email) {
        users.add(new User(name, city, email));
        System.out.println("Usuário registrado com sucesso: (" + name + ")");
    }

    public List<User> getUsers() {
        return users;
    }
}

class ParticipationController {
    private static final String FILENAME = "participacao.data";

    public void participateEvent(String eventName, String userName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILENAME, true))) {
            writer.println(eventName + ";" + userName);
            System.out.println("Participação registrada com sucesso: (" + userName + " participando de " + eventName + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Set<String>> getParticipations() {
        Map<String, Set<String>> participations = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String eventName = parts[0];
                    String userName = parts[1];
                    participations.putIfAbsent(eventName, new HashSet<>());
                    participations.get(eventName).add(userName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return participations;
    }
}

class EventController {
    private List<Event> events = new ArrayList<>();
    private List<String> categories = Arrays.asList("Festas", "Eventos esportivos", "Shows", "Casamentos");

    private static final String FILENAME = "events.data";

    public EventController() {
        loadEventsFromFile(); // Carregar eventos do arquivo ao iniciar o controlador
    }

    // Método para ler os eventos do arquivo
    private void loadEventsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    String name = parts[0];
                    String address = parts[1];
                    String category = parts[2];
                    Date dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(parts[3]);
                    String description = parts[4];
                    Event event = new Event(name, address, category, dateTime, description);
                    events.add(event);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void addEvent(String name, String address, String category, Date dateTime, String description) {
        if (categories.contains(category)) {
            Event newEvent = new Event(name, address, category, dateTime, description);
            events.add(newEvent);
            System.out.println("Evento adicionado com sucesso: (" + name + ")");
            saveEventToFile(newEvent);
        } else {
            System.out.println("Categoria inválida. Escolha uma das seguintes categorias: " + categories);
        }
    }

    private void saveEventToFile(Event event) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILENAME, true))) {
            writer.println(event.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Event> getAllEvents() {
        return events;
    }

    public List<Event> getUpcomingEvents() {
        Date now = new Date();
        return events.stream().filter(ev -> ev.getDateTime().after(now)).sorted(Comparator.comparing(Event::getDateTime)).toList();
    }

    public List<Event> getPastEvents() {
        Date now = new Date();
        return events.stream().filter(ev -> ev.getDateTime().before(now)).sorted(Comparator.comparing(Event::getDateTime).reversed()).toList();
    }

    public void displayCategories() {
        System.out.println("Categorias Disponíveis:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println(i + ". " + categories.get(i));
        }
    }
}

// View Classes
class UserView {
    public void displayUser(User user) {
        System.out.println("Usuário Registrado: " + user.getName() + ", " + user.getCity() + ", " + user.getEmail());
    }

    public void displayUsers(List<User> users) {
        System.out.println("Usuários Registrados:");
        for (User user : users) {
            System.out.println(user.getName());
        }
    }
}

class EventView {
    public void displayEvents(List<Event> events) {
        System.out.println("Todos os Eventos:");
        for (Event ev : events) {
            System.out.println("Nome: " + ev.getName());
            System.out.println("Endereço: " + ev.getAddress());
            System.out.println("Categoria: " + ev.getCategory());
            System.out.println("Data e Hora: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ev.getDateTime()));
            System.out.println("Descrição: " + ev.getDescription());
            System.out.println();
        }
    }

    public void displayParticipations(Map<String, Set<String>> participations) {
        System.out.println("Eventos e Usuários Participantes:");
        for (Map.Entry<String, Set<String>> entry : participations.entrySet()) {
            System.out.println("Evento: " + entry.getKey());
            System.out.println("Participantes: " + entry.getValue());
            System.out.println();
        }
    }
}

// Main Program
public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        UserController userController = new UserController();
        EventController eventController = new EventController();
        UserView userView = new UserView();
        EventView eventView = new EventView();
        ParticipationController participationController = new ParticipationController();

        boolean exit = false;
        while (!exit) {
            System.out.println("1. Registrar Usuário");
            System.out.println("2. Adicionar Evento");
            System.out.println("3. Mostrar todos os Eventos");
            System.out.println("4. Mostrar Próximos Eventos");
            System.out.println("5. Mostrar Eventos Passado");
            System.out.println("6. Participar do Evento");
            System.out.println("7. Listar Eventos e Usuários Participantes");
            System.out.println("8. Sair");
            System.out.print("Selecione uma Opção: ");
            String input = reader.readLine();
            if (input != null && !input.isEmpty()) {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        System.out.print("Informe o Nome: ");
                        String name = reader.readLine();
                        System.out.print("Informe a Cidade: ");
                        String city = reader.readLine();
                        System.out.print("Informe o Email: ");
                        String email = reader.readLine();
                        userController.registerUser(name, city, email);
                        break;
                    case 2:
                        System.out.print("Informe o Nome do Evento: ");
                        String eventName = reader.readLine();
                        System.out.print("Informe o endereço do Evento: ");
                        String eventAddress = reader.readLine();
                        System.out.print("Informe a Categoria do Evento\n[Festas, Eventos esportivos, Shows, Casamentos]: ");
                        String eventCategory = reader.readLine();
                        System.out.print("Enter event date and time (dd/MM/YYYY HH:mm): ");
                        Date eventDateTime = new Date();
                        try {
                            eventDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(reader.readLine());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.print("Informe a Descrição do Evento: ");
                        String eventDescription = reader.readLine();
                        eventController.addEvent(eventName, eventAddress, eventCategory, eventDateTime, eventDescription);
                        break;
                    case 3:
                        eventView.displayEvents(eventController.getAllEvents());
                        break;
                    case 4:
                        eventView.displayEvents(eventController.getUpcomingEvents());
                        break;
                    case 5:
                        eventView.displayEvents(eventController.getPastEvents());
                        break;
                    case 6:
                        List<User> users = userController.getUsers();
                        userView.displayUsers(users);
                        System.out.print("Selecione um Usuário pelo Nome: ");
                        String selectedUserName = reader.readLine();
                        List<Event> allEvents = eventController.getAllEvents();
                        eventView.displayEvents(allEvents);
                        System.out.print("Selecione um Evento pelo Nome: ");
                        String selectedEventName = reader.readLine();
                        participationController.participateEvent(selectedEventName, selectedUserName);
                        break;
                    case 7:
                        Map<String, Set<String>> participations = participationController.getParticipations();
                        eventView.displayParticipations(participations);
                        break;
                    case 8:
                        exit = true;
                        break;
                    default:
                        System.out.println("Opção Inválida, Selecione uma Opção Válida.");
                        break;
                }
            }
        }
    }
}
