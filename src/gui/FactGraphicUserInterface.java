package gui;

import client.Fact;
import client.FactClient;
import java.util.List;
import javax.swing.*;


public class FactGraphicUserInterface {
    private final FactClient client;
    private final JFrame jFrame = new JFrame("Fact Client");
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu menu = new JMenu("Действия");
    private final JMenuItem menuCreateItem = new JMenuItem("Добавить факт");
    private final JMenuItem menuReadItem = new JMenuItem("Найти факт по номеру");
    private final JMenuItem menuReadAllItem = new JMenuItem("Показать все факты");
    private final JMenuItem menuReadByAuthorItem = new JMenuItem("Показать факты автора");
    private final JMenuItem menuUpdateItem = new JMenuItem("Изменить факт");
    private final JMenuItem menuDeleteItem = new JMenuItem("Удалить факт");
    private final JTextArea mainText = new JTextArea("Загрузка");

    public FactGraphicUserInterface(FactClient client) {
        this.client = client;
    }

    public void createGUI() {
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(800, 500);
        jFrame.setVisible(true);
        menu.add(menuCreateItem);
        menu.add(menuReadItem);
        menu.add(menuReadAllItem);
        menu.add(menuDeleteItem);
        menu.add(menuUpdateItem);
        menu.add(menuReadByAuthorItem);
        menuBar.add(menu);
        JScrollPane scroll = new JScrollPane(mainText);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mainText.setFont(mainText.getFont().deriveFont(19f));
        jFrame.setJMenuBar(menuBar);
        jFrame.add(scroll);

        setDisplayAllFacts();
        initListeners();
    }

    public void setDisplayAllFacts() {
        SwingWorker<List<Fact>, Void> displayAllFactsWorker = new SwingWorker<>() {
            @Override
            protected List<Fact> doInBackground() throws Exception {
                return client.getAllFacts();
            }
            @Override
            protected void done() {
                try {
                    if (get() != null) {
                        StringBuilder sb = new StringBuilder();
                        for (Fact x : get()) {
                            sb.append(x.toString());
                            sb.append("\n");
                        }
                        mainText.setText(sb.toString());
                    } else mainText.setText("Факты недоступны");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        displayAllFactsWorker.execute();
    }

    public void setDisplayFactsByAuthor(String author){
        SwingWorker<List<Fact>, Void> displayFactsByAuthorWorker = new SwingWorker<>() {
            @Override
            protected List<Fact> doInBackground() throws Exception {
               return client.getByAuthor(author);
            }
            @Override
            protected void done() {
                StringBuilder sb = new StringBuilder();
                try {
                    if (get() != null) {
                        for (Fact x : get()) {
                            sb.append(x);
                            sb.append("\n");
                        }
                        mainText.setText(sb.toString());
                    } else mainText.setText("Нет фактов от данного автора");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        displayFactsByAuthorWorker.execute();
    }

    private void initListeners() {
        menuCreateItem.addActionListener(e -> {
            JPanel inputPanel = new JPanel();
            String author;
            String content;
           final String authorLabel = "Введите имя автора";
           final String contentLabel = "Введите факт";
            JTextField authorInput = new JTextField(10);
            JTextField contentInput = new JTextField( 50);
            inputPanel.add(new JLabel(authorLabel));
            inputPanel.add(authorInput);
            inputPanel.add(new JLabel(contentLabel));
            inputPanel.add(contentInput);
            if (JOptionPane.showConfirmDialog(jFrame, inputPanel, "Заполните данные", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                author = authorInput.getText();
                content = contentInput
                        .getText();
                Fact fact = new Fact(content, author, 0);
                String result = client.postFact(fact);
                JOptionPane.showMessageDialog(jFrame, result);
            }
            setDisplayAllFacts();
        });
        menuDeleteItem.addActionListener(e -> {
            JPanel inputPanel = new JPanel();
            final String idLabel = "Введите ID факта";
            int id;
            JTextField idInput = new JTextField(10);
            inputPanel.add(new JLabel(idLabel));
            inputPanel.add(idInput);
            if (JOptionPane.showConfirmDialog(jFrame, inputPanel, "Заполните данные", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    id = Integer.parseInt(idInput.getText());
                    String result = client.deleteFact(id);
                    JOptionPane.showMessageDialog(jFrame, result);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Некорректный ID");
                }
            }
            setDisplayAllFacts();
        });
        menuUpdateItem.addActionListener(e -> {
            JPanel inputPanel = new JPanel();
            String author;
            String content;
            int id;
            final String authorLabel = "Введите имя автора";
            final String contentLabel = "Введите факт";
            final String idLabel = "Введине ID факта";
            JTextField idInput = new JTextField(10);
            JTextField authorInput = new JTextField(10);
            JTextField contentInput = new JTextField( 50);
            inputPanel.add(new JLabel(idLabel));
            inputPanel.add(idInput);
            inputPanel.add(new JLabel(authorLabel));
            inputPanel.add(authorInput);
            inputPanel.add(new JLabel(contentLabel));
            inputPanel.add(contentInput);
            if (JOptionPane.showConfirmDialog(jFrame, inputPanel, "Заполните данные", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    id = Integer.parseInt(idInput.getText());
                    author = authorInput.getText();
                    content = contentInput.getText();
                    Fact fact = new Fact(content, author, id);
                    String result = client.updateFact(id, fact);
                    JOptionPane.showMessageDialog(jFrame, result);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Некорректные данные");
                }
            }
            setDisplayAllFacts();
        });
        menuReadByAuthorItem.addActionListener(e ->  {
            JPanel inputPanel = new JPanel();
            String author;
            JTextField authorInput = new JTextField(10);
            final String authorLabel = "Введите имя автора";
            inputPanel.add(new JLabel(authorLabel));
            inputPanel.add(authorInput);
            if (JOptionPane.showConfirmDialog(jFrame, inputPanel, "Введите имя автора", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                author = authorInput.getText();
                setDisplayFactsByAuthor(author);
            }
        });
        menuReadAllItem.addActionListener(e -> {
            setDisplayAllFacts();
        });
    }
}
