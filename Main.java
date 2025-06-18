import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    // МОДЕЛЬ
    static class User {
        String role;
        String login;
        String password;
        String fullName;

        User(String login, String password, String role) {
            this.login = login;
            this.password = password;
            this.role = role;
        }

        public String toString() {
            return role + " | " + login;
        }
    }

    static class Candidate extends User {
        String info;

        Candidate(String login, String password) {
            super(login, password, "Кандидат");
        }
    }

    static class Voting {
        String title;
        LocalDateTime endDate;
        List<Candidate> candidates = new ArrayList<>();
        Map<String, String> votes = new HashMap<>(); // login -> candidate

        Voting(String title, LocalDateTime endDate) {
            this.title = title;
            this.endDate = endDate;
        }
    }

    static class Model {
        List<User> users = new ArrayList<>();
        List<Voting> votings = new ArrayList<>();

        void addUser(User user) {
            users.add(user);
        }

        void removeUser(String login) {
            users.removeIf(u -> u.login.equals(login));
        }

        User findUser(String login, String password) {
            for (User u : users) {
                if (u.login.equals(login) && u.password.equals(password)) return u;
            }
            return null;
        }

        void createVoting(String title, LocalDateTime end) {
            votings.add(new Voting(title, end));
        }

        void exportResults(String path, boolean splitFiles) throws IOException {
            if (votings.isEmpty()) return;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            for (Voting v : votings) {
                String filename = v.title.replaceAll("\\s+", "_");
                if (filename.isEmpty()) filename = dtf.format(LocalDateTime.now());
                File file = new File(path, filename + ".txt");

                try (PrintWriter out = new PrintWriter(file)) {
                    out.println("Голосование: " + v.title);
                    Map<String, Integer> result = new HashMap<>();
                    for (String cand : v.votes.values()) {
                        result.put(cand, result.getOrDefault(cand, 0) + 1);
                    }
                    for (Map.Entry<String, Integer> entry : result.entrySet()) {
                        out.println("Кандидат: " + entry.getKey() + " — " + entry.getValue() + " голосов");
                    }
                }
                if (!splitFiles) break;
            }
        }
    }

    // ПРЕДСТАВЛЕНИЕ
    static class View {
        Scanner scanner = new Scanner(System.in);

        String prompt(String msg) {
            System.out.print(msg + ": ");
            return scanner.nextLine();
        }

        void show(String msg) {
            System.out.println(msg);
        }

        void menu(String[] options) {
            for (int i = 0; i < options.length; i++) {
                System.out.printf("%d. %s%n", i + 1, options[i]);
            }
        }

        int choose(int max) {
            System.out.print("Выберите пункт: ");
            return Integer.parseInt(scanner.nextLine());
        }
    }

    // КОНТРОЛЛЕР
    static class Controller {
        Model model;
        View view;

        Controller(Model m, View v) {
            this.model = m;
            this.view = v;
        }

        void start() {
            // Создаем одного администратора
            model.addUser(new User("admin", "admin", "Администратор"));
            while (true) {
                String login = view.prompt("Логин");
                String pass = view.prompt("Пароль");
                User user = model.findUser(login, pass);
                if (user != null) {
                    switch (user.role) {
                        case "Администратор": adminMenu(); break;
                        case "ЦИК": cikMenu(); break;
                        case "Кандидат": candidateMenu((Candidate) user); break;
                        case "Пользователь": userMenu(user); break;
                    }
                } else {
                    view.show("Неверный логин или пароль.");
                }
            }
        }

        void adminMenu() {
            while (true) {
                view.menu(new String[]{"Просмотр пользователей", "Удаление пользователя", "Создать ЦИК", "Выход"});
                int choice = view.choose(4);
                if (choice == 1) {
                    for (User u : model.users) view.show(u.toString());
                } else if (choice == 2) {
                    String login = view.prompt("Логин для удаления");
                    model.removeUser(login);
                } else if (choice == 3) {
                    String l = view.prompt("Логин ЦИК");
                    String p = view.prompt("Пароль");
                    model.addUser(new User(l, p, "ЦИК"));
                } else break;
            }
        }

        void cikMenu() {
            while (true) {
                view.menu(new String[]{"Создать голосование", "Добавить кандидата", "Сохранить результаты", "Назад"});
                int choice = view.choose(4);
                if (choice == 1) {
                    String title = view.prompt("Название голосования");
                    String dateStr = view.prompt("Дата окончания (ГГГГ-ММ-ДД)");
                    model.createVoting(title, LocalDateTime.parse(dateStr + "T00:00:00"));
                } else if (choice == 2) {
                    String login = view.prompt("Логин кандидата");
                    String pass = view.prompt("Пароль");
                    model.addUser(new Candidate(login, pass));
                } else if (choice == 3) {
                    String path = view.prompt("Папка для сохранения");
                    boolean split = view.prompt("Разделить по файлам? (y/n)").equalsIgnoreCase("y");
                    try {
                        model.exportResults(path.isEmpty() ? "." : path, split);
                        view.show("Результаты сохранены.");
                    } catch (IOException e) {
                        view.show("Ошибка сохранения: " + e.getMessage());
                    }
                } else break;
            }
        }

        void candidateMenu(Candidate c) {
            view.show("Добро пожаловать, " + c.login);
            c.info = view.prompt("Введите информацию о себе");
            view.show("Информация сохранена.");
        }

        void userMenu(User u) {
            while (true) {
                view.menu(new String[]{"Регистрация", "Голосование", "Просмотр кандидатов", "Назад"});
                int choice = view.choose(4);
                if (choice == 1) {
                    u.fullName = view.prompt("Введите ФИО");
                } else if (choice == 2) {
                    for (int i = 0; i < model.votings.size(); i++) {
                        view.show(i + 1 + ". " + model.votings.get(i).title);
                    }
                    int voteIndex = view.choose(model.votings.size()) - 1;
                    Voting v = model.votings.get(voteIndex);
                    for (int i = 0; i < v.candidates.size(); i++) {
                        view.show(i + 1 + ". " + v.candidates.get(i).login);
                    }
                    int candIndex = view.choose(v.candidates.size()) - 1;
                    v.votes.put(u.login, v.candidates.get(candIndex).login);
                    view.show("Голос учтён.");
                } else if (choice == 3) {
                    for (User user : model.users) {
                        if (user instanceof Candidate) view.show(user.login);
                    }
                } else break;
            }
        }
    }

    // ТОЧКА ВХОДА
    public static void main(String[] args) {
        Model model = new Model();
        View view = new View();
        Controller controller = new Controller(model, view);
        controller.start();
    }
}
