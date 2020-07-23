package com.javarush.task.task32.task3209;

import com.javarush.task.task32.task3209.listeners.FrameListener;
import com.javarush.task.task32.task3209.listeners.TabbedPaneChangeListener;
import com.javarush.task.task32.task3209.listeners.UndoListener;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View extends JFrame implements ActionListener {
    private Controller controller;

    private JTabbedPane tabbedPane =  new JTabbedPane(); // панель с двумя вкладками
    private JTextPane htmlTextPane = new JTextPane(); //  компонент для визуального редактирования html
    private JEditorPane plainTextPane = new JEditorPane(); // компонент для редактирования html в виде текста, он будет отображать код html

    private UndoManager undoManager = new UndoManager();
    private UndoListener undoListener = new UndoListener(undoManager);

    public View(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){
            ExceptionHandler.log(e);
        }
    }

    public Controller getController() {
        return controller;
    }

    public UndoListener getUndoListener() {
        return undoListener;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    //от интерфейса ActionListener и будет вызваться при выборе пунктов меню, у которых наше представление указано в виде слушателя событий
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case "Новый" :
                controller.createNewDocument();
                break;
            case "Открыть" :
                controller.openDocument();
                break;
            case "Сохранить" :
                controller.saveDocument();
            case "Сохранить как..." :
                controller.saveDocumentAs();
            case "Выход" :
                controller.exit();
            case "О программе" :
                this.showAbout();
        }
    }

    // отвечает за инициализацию преедставления
    public void init() {
        initGui(); // окно с графическим интерфейсом
        FrameListener frameListener = new FrameListener(this); // создаем подписчика
        addWindowListener(frameListener); // добавляем подписчика
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // закрытие
    }

    //инициализация меню
    public void initMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        MenuHelper.initFileMenu(this, jMenuBar);
        MenuHelper.initEditMenu(this, jMenuBar);
        MenuHelper.initStyleMenu(this, jMenuBar);
        MenuHelper.initAlignMenu(this, jMenuBar);
        MenuHelper.initColorMenu(this, jMenuBar);
        MenuHelper.initFontMenu(this, jMenuBar);
        MenuHelper.initHelpMenu(this, jMenuBar);
        getContentPane().add(jMenuBar, BorderLayout.NORTH);
    }

    //инициализация панелей редактора
    public void initEditor() {
        htmlTextPane.setContentType( "text/html"); // тип контента для компонента htmlTextPane
        tabbedPane.setPreferredSize(new Dimension(300, 300)); // устанавливаем предпочтительный размер панели
        tabbedPane.addTab("HTML", new JScrollPane(htmlTextPane)); // добавляем вкладку с именем "HTML" и созданным компонентом JScrollPane
        tabbedPane.addTab("Текст", new JScrollPane(plainTextPane)); //  добавляем вкладку с именем "Текст" и созданным компонентом JScrollPane
        tabbedPane.addChangeListener(new TabbedPaneChangeListener(this));
        getContentPane().add(tabbedPane, BorderLayout.CENTER); // Добавляем по центру панели контента текущего фрейма нашу панель с вкладками

    }

    //инициализация графического интерфейса
    public void initGui() {
        initMenuBar();
        initEditor();
        pack(); // устанавливает оптимальный размер для диалоговых окон
    }

    public boolean isHtmlTabSelected() {
        return tabbedPane.getSelectedIndex() == 0;
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

    public void undo() {
        try {
            undoManager.undo();
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }

    public void redo() {
        try {
            undoManager.redo();
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }

    // Сбрасывать правки в Undo менеджере
    public void resetUndo() {
        undoManager.discardAllEdits();
    }

    // выбрать вкладку с HTML
    public void selectHtmlTab() {
        tabbedPane.setSelectedIndex(0);
        resetUndo();
    }

    // получать документ у контроллера и устанавливать его в панель редактирования htmlTextPane
    public void update() {
        htmlTextPane.setDocument(controller.getDocument());
    }

    public void showAbout() {
        JOptionPane.showMessageDialog(tabbedPane.getSelectedComponent(), "Версия 1.0", "О программме", JOptionPane.INFORMATION_MESSAGE);
    }

    // Этот метод вызывается, когда произошла смена выбранной вкладки.
    public void selectedTabChanged() {
        if (tabbedPane.getSelectedIndex() == 0) {
            controller.setPlainText(plainTextPane.getText());
        }
        else if (tabbedPane.getSelectedIndex() == 1) {
            plainTextPane.setText(controller.getPlainText());
        }
        this.resetUndo();
    }

    public void exit() {
        controller.exit();
    }
}
