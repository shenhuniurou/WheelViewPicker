package com.xx.wheelview.utils;

import com.xx.wheelview.view.WaitDialog;

public interface DialogControl {

	public abstract void hideWaitDialog();

	public abstract WaitDialog showWaitDialog();

	public abstract void setWaitDialogMessage(String message);

	public abstract WaitDialog showWaitDialog(int resid);

	public abstract WaitDialog showWaitDialog(String text);
}
