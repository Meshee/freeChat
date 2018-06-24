package cn.meshee.freechat.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import com.reginald.editspinner.EditSpinner;
import butterknife.BindView;
import cn.meshee.freechat.R;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.presenter.BindAtPresenter;
import cn.meshee.freechat.ui.view.IBindAtView;
import cn.meshee.freechat.util.UIUtils;

public class BindActivity extends BaseActivity<IBindAtView, BindAtPresenter> implements IBindAtView {

    @BindView(R.id.etNick)
    EditText mEtNick;

    @BindView(R.id.vLineNick)
    View mVLineNick;

    @BindView(R.id.edit_spinner)
    EditSpinner editSpinner;

    @BindView(R.id.btnRegister)
    Button mBtnRegister;

    TextWatcher watcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBtnRegister.setEnabled(canRegister());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void initData() {
        super.initData();
        mPresenter.loadAccounts();
    }

    @Override
    public void initListener() {
        mEtNick.addTextChangedListener(watcher);
        mEtNick.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineNick.setBackgroundColor(UIUtils.getColor(R.color.green0));
            } else {
                mVLineNick.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });
        initSpinner();
        mBtnRegister.setOnClickListener(v -> {
            mPresenter.register();
        });
    }

    private void initSpinner() {
        String[] m = mPresenter.getAccountIds();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, m);
        editSpinner.setAdapter(adapter);
        editSpinner.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
            }
        });
        editSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    mEtNick.setText(mPresenter.getNickNameForAccount(position));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean canRegister() {
        return mEtNick.getText().toString().trim().length() > 0;
    }

    @Override
    protected BindAtPresenter createPresenter() {
        return new BindAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_bind;
    }

    @Override
    public EditText getEtNickName() {
        return mEtNick;
    }

    @Override
    public EditSpinner getSpRawId() {
        return editSpinner;
    }
}
