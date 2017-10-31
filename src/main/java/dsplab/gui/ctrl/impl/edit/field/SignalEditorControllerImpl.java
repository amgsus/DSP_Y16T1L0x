package dsplab.gui.ctrl.impl.edit.field;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.architecture.util.MessageBox;
import dsplab.common.Resources;
import dsplab.gui.ctrl.SignalEditorController;
import dsplab.io.signal.TembrFileReader;
import dsplab.io.signal.fa.SignalListIO;
import dsplab.io.signal.impl.tmb.TembrInfoHeader;
import dsplab.logic.signal.Signal;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SignalEditorControllerImpl extends SimpleController implements
    SignalEditorController
{
    public
    SignalEditorControllerImpl()
    {
        try {
            URL u = Resources.SIGNALEDITOR_FIELDS_FXML;
            livenMe(u);
        } catch (Throwable cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        initHandlers();
    }

    private Signal signal = null;

    @Override
    public
    void bind(Signal data)
    {
        if (data != null) {
            uiNameField.textProperty().bindBidirectional(
                data.getNameProperty()
            );
            uiSeriesColorBox.valueProperty().bindBidirectional(
                data.getSeriesColorProperty()
            );
            guiDataFromFileCheckbox.selectedProperty().bindBidirectional(
                data.getFileSourceProperty()
            );
            guiFileNameField.textProperty().bindBidirectional(
                data.getDataFileNameProperty()
            );
            guiChannelNoComboBox.valueProperty().bindBidirectional(
                data.getDataFileChannelProperty().asObject()
            );
            signal = data;

            if (guiFileNameField.getText() != null
                && !guiFileNameField.getText().isEmpty())
            {
                tryLoadTembrFile();
            }
        } else {
            if (signal != null) {
                uiNameField.textProperty().unbindBidirectional(
                    signal.getNameProperty()
                );
                uiSeriesColorBox.valueProperty().unbindBidirectional(
                    signal.getSeriesColorProperty()
                );
                guiDataFromFileCheckbox.selectedProperty().unbindBidirectional(
                    signal.getFileSourceProperty()
                );
                guiFileNameField.textProperty().unbindBidirectional(
                    signal.getDataFileNameProperty()
                );
                guiChannelNoComboBox.valueProperty().unbindBidirectional(
                    signal.getDataFileChannelProperty().asObject()
                );
                signal = null;
            }
        }
    }

    @Override
    public
    void unbind()
    {
        bind(null);
    }

    protected
    void tryLoadTembrFile()
    {
        File file = new File(guiFileNameField.getText());

        try {
            TembrFileReader tf = SignalListIO.newTembrFileReader(file);

            TembrInfoHeader info = tf.readInfo();

            int prevSelChannel = guiChannelNoComboBox.getValue();

            ObservableList<Integer> chItems = guiChannelNoComboBox.getItems();
            chItems.clear();

            for ( int i = 0; i < info.channels; i++ ) chItems.add(i);

            if (chItems.size() > prevSelChannel)
                guiChannelNoComboBox.getSelectionModel().select(prevSelChannel);
            else if (chItems.size() > 0)
                guiChannelNoComboBox.getSelectionModel().selectFirst();

        } catch (IOException e) {
            String msg = "Failed to open the file:\n\n%s";
            MessageBox.getWarnBox(String.format(msg, e.getMessage()));
        }
    }

    protected
    void initHandlers()
    {
        guiFileNameField.disableProperty().bind(
            guiDataFromFileCheckbox.selectedProperty().not());
        guiBrowseFileButton.disableProperty().bind(
            guiDataFromFileCheckbox.selectedProperty().not());
        guiChannelNoComboBox.disableProperty().bind(
            guiDataFromFileCheckbox.selectedProperty().not());

        guiBrowseFileButton.setOnAction(event -> {
            FileChooser dlg = SignalListIO.newFileChooserTembr();
            dlg.setTitle("Choose Tembr's signal from a file...");

            File file;

            if ((file = dlg.showOpenDialog(null)) != null) {
                try
                {
                    guiFileNameField.setText(file.getCanonicalPath());
                    tryLoadTembrFile();
                }
                catch (IOException e)
                {
                    String msg = "Failed to open the specified file:\n\n%s";
                    MessageBox.getWarnBox(String.format(msg, e.getMessage()));
                }
            }
        });
    }

    @FXML
    private
    TextField uiNameField;

    @FXML
    private
    ColorPicker uiSeriesColorBox;

    @FXML
    private
    CheckBox guiDataFromFileCheckbox;

    @FXML
    private
    TextField guiFileNameField;

    @FXML
    private
    Button guiBrowseFileButton;

    @FXML
    private
    ComboBox<Integer> guiChannelNoComboBox;
}
