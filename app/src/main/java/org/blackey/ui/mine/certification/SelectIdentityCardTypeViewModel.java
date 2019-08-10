package org.blackey.ui.mine.certification;

import android.app.Application;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.R;
import org.blackey.entity.CountryOrRegion;
import org.blackey.entity.UserCertification;
import org.blackey.model.request.UserCertificationRequest;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.ui.choose.country.ChooseCountryOrRegionListActivity;
import org.blackey.ui.choose.country.ChooseCountryOrRegionListItemViewModel;
import com.vondear.rxtool.RxTool;
import com.vondear.rxtool.view.RxToast;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.utils.KLog;

public class SelectIdentityCardTypeViewModel extends ToolbarViewModel {


    public  UserCertificationRequest request = new UserCertificationRequest();
    public ObservableField<String> nationalityText = new ObservableField<>();

    public ObservableField<Boolean> identityCardChecked = new ObservableField<>(true);
    public ObservableField<Boolean> passportChecked = new ObservableField<>(false);



    public SelectIdentityCardTypeViewModel(@NonNull Application application) {
        super(application);
    }

    private static final String TOKEN_CERTIFICATION_VIEW_MODEL_CHOOSE_COUNTRY = "token_certification_view_model_choose_country";
    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.certification));
        setRightIconVisible(View.GONE);

    }

    public void  initData(){
        request.setIdentityCardType(UserCertification.IDENTITY_CARD_TYPE_DOMESTIC_IDENTITY_CARD);

    }

    //选择 国家/地区 点击事件
    public BindingCommand chooseCountryOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            Bundle bundle = new Bundle();
            bundle.putString(ChooseCountryOrRegionListItemViewModel.TOKEN_CHOOSE_COUNTRY_REFRESH,TOKEN_CERTIFICATION_VIEW_MODEL_CHOOSE_COUNTRY);

            startActivity(ChooseCountryOrRegionListActivity.class,bundle);
        }
    });

    public void initMessenger(){

        Messenger.getDefault().register(RxTool.getContext(), TOKEN_CERTIFICATION_VIEW_MODEL_CHOOSE_COUNTRY, CountryOrRegion.class, new BindingConsumer<CountryOrRegion>() {
            @Override
            public void call(CountryOrRegion o) {
                KLog.i(o);
                request.setNationality(o.getCountryId());
                nationalityText.set(o.getName());
            }
        } );
    }

    public BindingCommand<Boolean> identityCardCheckedChangeCommand = new BindingCommand(new BindingConsumer<Boolean>() {

        @Override
        public void call(Boolean isChecked) {
            passportChecked.set(!isChecked);
            identityCardChecked.set(isChecked);
            if(isChecked){
                request.setIdentityCardType(UserCertification.IDENTITY_CARD_TYPE_DOMESTIC_IDENTITY_CARD);
            }

        }
    });

    public BindingCommand<Boolean> passportCheckedChangeCommand = new BindingCommand(new BindingConsumer<Boolean>() {

        @Override
        public void call(Boolean isChecked) {
            passportChecked.set(isChecked);
            identityCardChecked.set(!isChecked);
            if(isChecked){
                request.setIdentityCardType(UserCertification.IDENTITY_CARD_TYPE_PASSPORT);
            }

        }
    });

    /**
     * 下一步
     * @return
     */
    public BindingCommand nextClickCommand() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {

                if(request.getNationality() ==null ){
                    RxToast.showToast(R.string.hint_country);
                    return;
                }
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("request", request);
                if(identityCardChecked.get()){
                    mBundle.putInt("title",R.string.identity_card);
                }
                if(passportChecked.get()){
                    mBundle.putInt("title",R.string.passport);
                }


                startContainerActivity(UploadCredentialsFragment.class.getCanonicalName(),mBundle);
            }
        });
    }
}
