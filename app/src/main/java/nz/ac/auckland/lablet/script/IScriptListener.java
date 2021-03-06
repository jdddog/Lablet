/*
 * Copyright 2013-2014.
 * Distributed under the terms of the GPLv3 License.
 *
 * Authors:
 *      Clemens Zeidler <czei002@aucklanduni.ac.nz>
 */
package nz.ac.auckland.lablet.script;


/**
 * Listener interface for a script.
 */
public interface IScriptListener {
    void onComponentStateChanged(ScriptTreeNode current, int state);
}
