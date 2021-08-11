package lhg.common.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class UndoRedo {

    public static final long EmptyCommandId = -1;

    long cmdId = 0;
    Stack<Command> commandsCanUndo = new Stack<>();
    Stack<Command> commandsCanRedo = new Stack<>();
    Callback callback;

    EditText editText;
    final TextWatcher textWatcher = new TextWatcher() {
        CharSequence textBefore;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count == 0) {
                textBefore = "";
            } else {
                textBefore = s.subSequence(start, start + count);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            CharSequence textAfter = "";
            if (count > 0) {
                textAfter = s.subSequence(start, start+count);
            }
            Command cmd = Command.obtain();
            cmd.id = cmdId++;
            cmd.init(start, textBefore, textAfter);
            commandsCanUndo.add(cmd);
            clearCommandsCanRedo();
        }

        @Override
        public void afterTextChanged(Editable s) {
            notifyCallback();
        }
    };

    public void init(EditText editText) {
        this.editText = editText;
        editText.addTextChangedListener(textWatcher);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void clearCommandsCanRedo() {
        while (!commandsCanRedo.empty()) {
            Command.recycle(commandsCanRedo.pop());
        }
    }

    public long getLastCanUndoId() {
        if (commandsCanUndo.isEmpty()) {
            return EmptyCommandId;
        }
        return commandsCanUndo.peek().id;
    }

    public boolean canUndo() {
        return !commandsCanUndo.isEmpty();
    }

    public boolean canRedo() {
        return !commandsCanRedo.isEmpty();
    }

    public void undo() {
        if (commandsCanUndo.isEmpty()) {
            return;
        }
        editText.removeTextChangedListener(textWatcher);
        Command c = commandsCanUndo.pop();
        editText.getText().replace(c.start, c.afterEnd(), c.textBefore);
        editText.setSelection(c.beforeEnd());
        commandsCanRedo.push(c);
        notifyCallback();
        editText.addTextChangedListener(textWatcher);
    }

    public void redo() {
        if (commandsCanRedo.isEmpty()) {
            return;
        }
        editText.removeTextChangedListener(textWatcher);
        Command c = commandsCanRedo.pop();
        editText.getText().replace(c.start, c.beforeEnd(), c.textAfter);
        editText.setSelection(c.afterEnd());
        commandsCanUndo.push(c);
        notifyCallback();
        editText.addTextChangedListener(textWatcher);
    }

    private void notifyCallback() {
        if (callback != null) {
            callback.call(commandsCanUndo.size(), commandsCanRedo.size());
        }
    }

    public interface Callback {
        void call(int countCanUndo, int countCanRedo);
    }

    private static class Command {
        static List<Command> caches = new ArrayList<>(100);
        CharSequence textBefore, textAfter;
        int start;
        long id = 0;

        int beforeCount() {
            return textBefore.length();
        }
        int beforeEnd() {
            return start + beforeCount();
        }
        int afterCount() {
            return textAfter.length();
        }
        int afterEnd() {
            return start + afterCount();
        }

        void init(int start, CharSequence textBefore, CharSequence textAfter) {
            this.start = start;
            this.textBefore = textBefore == null ? "" : textBefore;
            this.textAfter = textAfter == null ? "" : textAfter;
        }

        static void recycle(Command c) {
            if (caches.size() >= 100) {
                return;
            }
            caches.add(c);
        }

        static Command obtain() {
            if (caches.isEmpty()) {
                return new Command();
            }
            return caches.remove(caches.size() - 1);
        }
    }
}
