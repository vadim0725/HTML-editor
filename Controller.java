package com.javarush.task.task32.task3209;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;

public class Controller {
    private View view; // представление
    private HTMLDocument document; // модель
    private File currentFile; // файл который открыт в редакторе

    public Controller(View view) {
        this.view = view;
    }

    public HTMLDocument getDocument() {
        return document;
    }

    public static void main(String[] args) {
        View view = new View(); //Создаем объект представления
        Controller controller = new Controller(view); // Создаем контроллер, используя представление
        view.setController(controller); // Устанавливаем у представления контроллер
        view.init(); // Инициализируем представление
        controller.init(); // Инициализируем контроллер
    }
    // отвечает за инициализацию контроллера
    public void init() {
        createNewDocument();
    }

    public void resetDocument() {
        if (document != null)
        document.removeUndoableEditListener(view.getUndoListener());

        document = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
        document.addUndoableEditListener(view.getUndoListener());
        view.update();
    }

    public void setPlainText(String text) {
        resetDocument();
        StringReader stringReader = new StringReader(text);
        try {
            new HTMLEditorKit().read(stringReader, document, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            ExceptionHandler.log(e);
        }
    }

    public String getPlainText() {
        StringWriter stringWriter = new StringWriter();
        try {
            new HTMLEditorKit().write(stringWriter, document, 0, document.getLength());
        } catch (IOException | BadLocationException e) {
            ExceptionHandler.log(e);
        }
        return stringWriter.toString();
    }

    public void createNewDocument() {
        view.selectHtmlTab();
        resetDocument();
        view.setTitle("HTML редактор");
        view.resetUndo();
        currentFile = null;
    }

    public void openDocument() {
        view.selectHtmlTab(); //переключать представление на html вкладку
        JFileChooser jFileChooser = new JFileChooser(); //создавать новый объект для выбора файла JFileChooser
        jFileChooser.setFileFilter(new HTMLFileFilter()); // устанавливать объекту класса JFileChooser в качестве фильтра объект HTMLFileFilter
        jFileChooser.setDialogTitle("Open File"); //показывать диалоговое окно "Open File" для выбора файла
        if(jFileChooser.showOpenDialog((view)) == JFileChooser.APPROVE_OPTION){ // Если пользователь подтвердит выбор файла
            resetDocument();
            currentFile = jFileChooser.getSelectedFile(); // установить новое значение currentFile
            view.setTitle(currentFile.getName()); // Устанавливать имя файла в качестве заголовка окна представления
            view.resetUndo();
            try {
                FileReader fileReader = new FileReader(currentFile); // Создавать FileWriter на базе currentFile
                new HTMLEditorKit().read(fileReader, document, 0); // Переписывать данные из document в объект FileWriter

                fileReader.close();
            } catch (IOException | BadLocationException e) {
                ExceptionHandler.log(e);
            }
        }

    }

    public void saveDocument() {
        view.selectHtmlTab();
        if (currentFile != null) {
            try {
                FileWriter fileWriter = new FileWriter(currentFile); // Создавать FileWriter на базе currentFile
                new HTMLEditorKit().write(fileWriter, document, 0, document.getLength()); // Переписывать данные из document в объект FileWriter
                fileWriter.close();
            } catch (IOException | BadLocationException e) {
                ExceptionHandler.log(e);
            }
        }

        if (currentFile == null)
            saveDocumentAs();
    }

    public void saveDocumentAs() {
        view.selectHtmlTab(); // Переключать представление на html вкладку
        JFileChooser jFileChooser = new JFileChooser(); // Создавать новый объект для выбора файла
        jFileChooser.setFileFilter(new HTMLFileFilter()); // Устанавливать ему в качестве фильтра объект
        jFileChooser.setDialogTitle("Save File"); // Показывать диалоговое окно "Save File" для выбора файла
        if(jFileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION){ // Если пользователь подтвердит выбор файла
            currentFile = jFileChooser.getSelectedFile(); // Сохранять выбранный файл в поле currentFile
            view.setTitle(currentFile.getName()); // Устанавливать имя файла в качестве заголовка окна представления
            try {
                FileWriter fileWriter = new FileWriter(currentFile); // Создавать FileWriter на базе currentFile
                new HTMLEditorKit().write(fileWriter, document, 0, document.getLength()); // Переписывать данные из document в объект FileWriter
                fileWriter.close();
            } catch (IOException | BadLocationException e) {
                ExceptionHandler.log(e);
            }
        }
    }

    public void exit() {
        System.exit(0);
    }
}
