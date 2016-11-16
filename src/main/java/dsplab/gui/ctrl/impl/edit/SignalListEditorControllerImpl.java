package dsplab.gui.ctrl.impl.edit;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.callback.DelegateWrapper;
import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.Controllers;
import dsplab.gui.component.edit.impl.SETreeViewCell;
import dsplab.gui.ctrl.HarmonicEditorController;
import dsplab.gui.ctrl.SignalEditorController;
import dsplab.gui.ctrl.SignalListEditorController;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.Signal;
import dsplab.logic.signal.util.SigUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static dsplab.common.Const.HARMONICEDITOR;
import static dsplab.common.Const.SIGNALEDITOR;
import static dsplab.gui.util.Hei.cast;
import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.TOP_LEFT;

public class SignalListEditorControllerImpl extends SimpleController implements
    SignalListEditorController
{
    public SignalListEditorControllerImpl()
    {
        try {
            URL u = Resources.SIGNALEDITOR_FXML;
            livenMe(u);
        } catch (Throwable cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        initComponentHandlers();
        initComponentsBehavior();

        this.signalEditorController =
            Controllers.getFactory().giveMeSomethingLike(SIGNALEDITOR);
        this.harmonicEditorController =
            Controllers.getFactory().giveMeSomethingLike(HARMONICEDITOR);

        this.guiTreeView.setCellFactory(p -> new SETreeViewCell());
        this.guiTreeView.setShowRoot(false);
/*

        // Test...

        Signal s1 = new Signal("Signal #1", Color.RED);
        s1.getHarmonics().add(new Harmonic(1.0, 30, 500));
        s1.getHarmonics().add(new Harmonic(1.1253, 0, 111));
        s1.getHarmonics().add(new Harmonic(9, 12.5, 1));

        Signal s2 = new Signal("Signal #2", Color.GREEN);
        s2.getHarmonics().add(new Harmonic());

        Signal s3 = new Signal("Signal #3", Color.BLUE);
        s3.getHarmonics().add(new Harmonic());
        s3.getHarmonics().add(new Harmonic());
        s3.getHarmonics().add(new Harmonic());
        s3.getHarmonics().add(new Harmonic());
        s3.getHarmonics().add(new Harmonic());

        Signal s4 = new Signal("Signal #4", Color.MEDIUMPURPLE);

        this.sourceList = new ArrayList<>(Arrays.asList(s1, s2, s3, s4));
        refreshList();
*/
    }

    // -------------------------------------------------------------------- //

    private
    void setFieldsBoxContent(Node content)
    {
        this.guiFieldsBox.getChildren().clear();
        if (content != null) {
            this.guiFieldsBox.getChildren().add(content);
        }
    }

    // -------------------------------------------------------------------- //

    private final List<Signal> sourceList = new ArrayList<>();

    @Override
    public
    void setEditableList(List<Signal> list)
    {
        if (list == null)
            throw new IllegalArgumentException("list is null");

        sourceList.clear();

        SigUtils.cloneSignalList(list, this.sourceList);
        refreshList();
    }

    @Override
    public
    List<Signal> getModifiedList()
    {
        return sourceList;
    }

    // -------------------------------------------------------------------- //

    public
    void refreshList()
    {
        if (Platform.isFxApplicationThread()) {
            impl_refreshTreeView(sourceList, guiTreeView);
        } else {
            Platform.runLater(() -> impl_refreshTreeView(sourceList,
                guiTreeView));
        }
    }

    private
    void impl_refreshTreeView(List<Signal> signalList,
        TreeView<Object> treeView)
    {
        treeView.setRoot(null);

        TreeItem<Object> root = new TreeItem<>(null);

        for (Signal signal : signalList) {

            TreeItem<Object> signalItem = new TreeItem<>(signal);

            for (Harmonic harmonic : signal.getHarmonics()) {
                TreeItem<Object> h = new TreeItem<>(harmonic);
                signalItem.getChildren().add(h);
            }

            root.getChildren().add(signalItem);
        }

        treeView.setRoot(root);
    }

    // -------------------------------------------------------------------- //

    /*
     * OK
     */
    private void initComponentHandlers()
    {
        guiOKButton.setOnAction(event -> {
            okDelegate.execute();
        });

        guiCancelButton.setOnAction(event -> {
            sourceList.clear();
            cancelDelegate.execute();
        });

        /*
         * Adds a harmonic if a harmonic is selected in the tree view.
         * Otherwise adds a signal.
         */
        guiAddButton.setOnAction(event -> {
            TreeItem i = guiTreeView.getSelectionModel().getSelectedItem();

            if (i == null || i.getValue() instanceof Signal) {
                guiSignalAddMenuItem.getOnAction().handle(event);
            } else {
                if (i.getValue() instanceof Harmonic) {
                    guiHarmonicAddMenuItem.getOnAction().handle(event);
                }
            }
        });

        /*
         * Adds a harmonic to a signal selected in the tree view.
         */
        guiHarmonicAddMenuItem.setOnAction(event -> {
            TreeItem<Object> i =
                guiTreeView.getSelectionModel().getSelectedItem();

            if (i.getValue() instanceof Harmonic) {
                i = i.getParent();
            }

            Signal signal = cast(i.getValue());

            Harmonic h = new Harmonic();
            signal.getHarmonics().add(h);

            TreeItem<Object> newItem = new TreeItem<>(h);
            i.getChildren().add(newItem);

            guiTreeView.getSelectionModel().select(newItem);
        });

        /*
         * Adds a signal.
         */
        guiSignalAddMenuItem.setOnAction(event -> {
            TreeItem<Object> i = guiTreeView.getRoot();

            Signal signal = new Signal();
            sourceList.add(signal);

            TreeItem<Object> newItem = new TreeItem<>(signal);
            i.getChildren().add(newItem);

            guiTreeView.getSelectionModel().select(newItem);
        });

        /*
         * Removes a selected item from the tree view.
         */
        guiDeleteButton.setOnAction(event -> {
            TreeItem i = guiTreeView.getSelectionModel().getSelectedItem();

            Object value = i.getValue();

            if (value instanceof Signal)
            {
                sourceList.remove((Signal) value);
            }
            else
            {
                if (value instanceof Harmonic)
                {
                    TreeItem p = i.getParent();
                    Signal signal = (Signal) p.getValue();
                    signal.getHarmonics().remove((Harmonic) value);
                }
            }

            i.getParent().getChildren().remove(i);
            guiTreeView.getSelectionModel().clearSelection();
        });

        /*
         * Shows appropriate fields for a selected item (signal/harmonic).
         */
        guiTreeView.getSelectionModel()
            .selectedItemProperty().addListener(p -> {

            TreeItem item =
                this.guiTreeView.getSelectionModel().getSelectedItem();

            if (item != null && item.getValue() != null) {

                Object obj = item.getValue();

                signalEditorController.unbind();
                harmonicEditorController.unbind();

                if (obj instanceof Signal) {
                    setFieldsBoxContent(signalEditorController.getFxRoot());
                    guiFieldsBox.setAlignment(TOP_LEFT);

                    Signal s = cast(obj);
                    signalEditorController.bind(s);

                } else {
                    if (obj instanceof Harmonic) {
                        setFieldsBoxContent(harmonicEditorController
                            .getFxRoot());
                        guiFieldsBox.setAlignment(TOP_LEFT);

                        Harmonic h = cast(obj);
                        harmonicEditorController.bind(h);

                    } else {
                        guiFieldsBox.setAlignment(CENTER);
                        setFieldsBoxContent(guiFieldsPlaceholderLabel);
                    }
                }

            } else {
                guiFieldsBox.setAlignment(CENTER);
                setFieldsBoxContent(guiFieldsPlaceholderLabel);
            }
        });

        // ...
    }

    /*
     * OK
     */
    private void initComponentsBehavior()
    {
        /*
         * Disables the "Delete" button if no item is selected in the
         * tree view.
         */
        guiDeleteButton.disableProperty().bind(guiTreeView
            .selectionModelProperty().get().selectedItemProperty().isNull());

        /*
         * Disables the "Delete" button if no item is selected in the
         * tree view. It prevents NullPointerException.
         */
        guiHarmonicAddMenuItem.disableProperty().bind(guiTreeView
            .selectionModelProperty().get().selectedItemProperty().isNull());

        // ...
    }

    // --------------------------------------------------------------------- //


    private SignalEditorController signalEditorController;
    private HarmonicEditorController harmonicEditorController;

    @FXML
    private HBox guiHBox; // Root

    @FXML
    private Button guiOKButton;

    @FXML
    private Button guiCancelButton;

    @FXML
    private VBox guiFieldsBox;

    @FXML
    private TreeView<Object> guiTreeView;

    @FXML
    private SplitMenuButton guiAddButton;

    @FXML
    private Button guiDeleteButton;

    @FXML
    private MenuItem guiSignalAddMenuItem;

    @FXML
    private MenuItem guiHarmonicAddMenuItem;

    @FXML
    private MenuItem guiAddLikeSelectedMenuItem;

    @FXML
    private MenuItem guiDeleteSelectedMenuItem;

    @FXML
    private Label guiFieldsPlaceholderLabel;

    protected final DelegateWrapper okDelegate = new DelegateWrapper();
    protected final DelegateWrapper cancelDelegate = new DelegateWrapper();

    @Override
    public void setOnOK(Delegate okDelegate)
    {
        if (okDelegate != null) {
            this.okDelegate.wrapDelegate(okDelegate);
        } else {
            this.okDelegate.removeDelegate();
        }
    }

    @Override
    public void setOnCancel(Delegate caDelegate)
    {
        if (caDelegate != null) {
            this.cancelDelegate.wrapDelegate(caDelegate);
        } else {
            this.cancelDelegate.removeDelegate();
        }
    }
}
