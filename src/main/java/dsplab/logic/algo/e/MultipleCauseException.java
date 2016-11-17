package dsplab.logic.algo.e;

import javafx.collections.FXCollections;

import java.util.Collections;
import java.util.List;

public class MultipleCauseException extends Exception
{
    public MultipleCauseException(List<Throwable> causes)
    {
        super();
        causeList = causes;
    }

    public MultipleCauseException(String message, List<Throwable> causes)
    {
        super(message);
        causeList = causes;
    }

    protected final List<Throwable> causeList;

    public
    final List<Throwable> getCauseList()
    {
        return Collections.unmodifiableList(causeList);
    }
}
