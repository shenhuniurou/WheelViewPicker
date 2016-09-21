package com.zj.mpocket.utils;

import com.zj.mpocket.view.WaitDialog;

public interface DialogControl {

	public abstract void hideWaitDialog();

	public abstract WaitDialog showWaitDialog();

	public abstract void setWaitDialogMessage(String message);

	public abstract WaitDialog showWaitDialog(int resid);

	public abstract WaitDialog showWaitDialog(String text);
}
