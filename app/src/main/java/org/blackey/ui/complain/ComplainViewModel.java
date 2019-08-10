package org.blackey.ui.complain;

import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.vondear.rxtool.view.RxToast;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.service.ComplainApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.ui.choose.img.ChooseImageListItemViewModel;
import org.blackey.utils.RetrofitClient;

import java.io.File;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ComplainViewModel extends ToolbarViewModel implements ChooseImageListItemViewModel.Listener {


    ChooseImageListItemViewModel addView = new ChooseImageListItemViewModel(ComplainViewModel.this,this);



   public ObservableField<String> contactText = new ObservableField<>("");

    public ObservableField<String> contentText = new ObservableField<>("");


    public void addImage(String path) {

        ChooseImageListItemViewModel itemViewModel = new ChooseImageListItemViewModel(this,this,path);

        observableList.add(observableList.size()-1,itemViewModel);

        if(observableList.size() > 3){
            observableList.remove(addView);
        }
    }

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();
    public class UIChangeObservable {

        public ObservableBoolean showRxDialogChooseImage = new ObservableBoolean(false);

        public ObservableField<String> showRxDialogScaleView = new ObservableField("");
    }


    @Override
    public void itemClick(String url) {
        if(!TextUtils.isEmpty(url)){

            RxToast.showToast(url);
            uc.showRxDialogScaleView.set(url);

        }else{

            uc.showRxDialogChooseImage.set(!uc.showRxDialogChooseImage.get());
       }
    }

    @Override
    public void deleteClick(ChooseImageListItemViewModel itemViewModel) {
        observableList.remove(itemViewModel);
        if(observableList.size() < 3 && !observableList.contains(addView)){

            observableList.add(addView);
        }
    }



    public ComplainViewModel(@NonNull Application application) {
        super(application);
    }



    //给RecyclerView添加ObservableList
    public ObservableList<ChooseImageListItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<ChooseImageListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_choose_image);

    public final BindingRecyclerViewAdapter<ChooseImageListItemViewModel> adapter = new BindingRecyclerViewAdapter<>();

    public void initToolbar() {
        setTitleText(getApplication().getString(R.string.title_complain));
        setRightIconVisible(View.GONE);
        setRightTextVisible(View.GONE);

        observableList.add(addView);

    }


    public BindingCommand submitClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {

            if(TextUtils.isEmpty(contactText.get())){

                RxToast.showToast(getApplication().getString(R.string.hint_contact));

                return;
            }

            if(TextUtils.isEmpty(contentText.get())){
                RxToast.showToast(getApplication().getString(R.string.hint_complaint_content));

                return;
            }

            MultipartBody.Builder builder = new MultipartBody.Builder();


            for(ChooseImageListItemViewModel itemViewModel : observableList){
                if(!TextUtils.isEmpty(itemViewModel.url.get())){
                    File file =  new File(itemViewModel.url.get());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
                    builder.addFormDataPart("images", file.getName(), requestBody);
                }
            }
            builder.addFormDataPart("contact",contactText.get());
            builder.addFormDataPart("content",contentText.get());

            builder.setType(MultipartBody.FORM);
            MultipartBody multipartBody = builder.build();


            RetrofitClient.getInstance().create(ComplainApiService.class)
                    .add(multipartBody)
                    .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                    .compose(RxUtils.schedulersTransformer()) //线程调度
                    .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            showDialog(getApplication().getString(R.string.loading));
                        }
                    })
                    .subscribe(new Consumer<BaseResponse<String>>() {
                        @Override
                        public void accept(BaseResponse<String> response) throws Exception {
                            dismissDialog();
                            //请求成功
                            if(response.isOk()){
                                RxToast.showToast(R.string.message_complain_success);
                                finish();
                            }else{
                                RxToast.showToast(response.getMessage());
                            }

                        }
                    }, new Consumer<ResponseThrowable>() {
                        @Override
                        public void accept(ResponseThrowable throwable) throws Exception {
                            dismissDialog();
                            //请求刷新完成收回
                            throwable.printStackTrace();
                            RxToast.error(throwable.message);
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            dismissDialog();
                            //关闭对话框
                            //请求刷新完成收回
                        }
                    });

        }
    });

}
