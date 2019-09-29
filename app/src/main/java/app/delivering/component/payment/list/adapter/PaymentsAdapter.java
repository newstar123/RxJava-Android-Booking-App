package app.delivering.component.payment.list.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import app.R;
import app.delivering.mvp.payment.list.delete.model.PaymentsDeleteModel;
import app.delivering.mvp.payment.list.init.events.PaymentsInitBinderModelEvent;
import app.delivering.mvp.payment.list.init.model.PaymentsInitBinderModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.rambler.libs.swipe_layout.SwipeLayout;
import rx.Observable;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.ViewHolder> {
    private List<PaymentsInitBinderModel> models;
    private SwipeLayout swipeLayout;
    private static int sizeOfModels;

    public PaymentsAdapter() {
        this.models = Collections.emptyList();
    }

    public void updateRegular(String cardId) {
        Observable.from(models).doOnNext(model -> model.setDefaultCard(View.GONE)).subscribe();
        PaymentsInitBinderModel paymentsInitBinderModel = Observable.from(models)
                .filter(model -> cardId.equals(model.getCardId()))
                .toBlocking()
                .firstOrDefault(new PaymentsInitBinderModel());
        paymentsInitBinderModel.setDefaultCard(View.VISIBLE);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.payments_item_icon) ImageView iconImageView;
        @BindView(R.id.payments_item_card_number) TextView numberTextView;
        @BindView(R.id.payments_item_default) ImageView defaultImageView;
        @BindView(R.id.payments_item_root) RelativeLayout rootRelativeLayout;
        @BindView(R.id.swipe_layout) SwipeLayout swipeLayout;
        @BindView(R.id.right_view) FrameLayout rightView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payments, parent, false);
        return new ViewHolder(itemView);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentsInitBinderModel singlePaymentsModel = models.get(position);
        holder.defaultImageView.setVisibility(singlePaymentsModel.getDefaultCard());
        holder.iconImageView.setBackgroundResource(singlePaymentsModel.getBrand());
        holder.numberTextView.setText(singlePaymentsModel.getHiddenCardNumber());
        holder.rootRelativeLayout.setOnClickListener(view -> onClick(singlePaymentsModel));
        holder.rightView.setOnClickListener(v -> {
            removeItem(singlePaymentsModel, position);
            holder.numberTextView.setVisibility(View.VISIBLE);
            holder.swipeLayout.reset();
        });
        holder.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
            @Override public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {
                if (PaymentsAdapter.this.swipeLayout != null && PaymentsAdapter.this.swipeLayout != swipeLayout) {
                    RelativeLayout cardContainer = PaymentsAdapter.this.swipeLayout.findViewById(R.id.payments_item_root);
                    TextView cardNumber = cardContainer.findViewById(R.id.payments_item_card_number);
                    cardNumber.setVisibility(View.VISIBLE);
                    PaymentsAdapter.this.swipeLayout.animateReset();
                }
            }
            @Override public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) { }
            @Override public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) { }
            @Override public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                if (moveToRight)
                    holder.numberTextView.setVisibility(View.VISIBLE);
                else
                    holder.numberTextView.setVisibility(View.GONE);
                PaymentsAdapter.this.swipeLayout = swipeLayout;
            }
        });
    }

    public void removeItem(PaymentsInitBinderModel paymentsInitBinderModel, int position) {
        PaymentsDeleteModel paymentsDeleteModel = new PaymentsDeleteModel();
        paymentsDeleteModel.setPaymentsInitBinderModel(paymentsInitBinderModel);
        paymentsDeleteModel.setPosition(position);
        EventBus.getDefault().post(paymentsDeleteModel);
        models.remove(paymentsInitBinderModel);
        notifyItemRemoved(position);
        sizeOfModels = models.size();
    }

    private void onClick(PaymentsInitBinderModel modelEvent) {
        if (sizeOfModels > 1)
            EventBus.getDefault().post(new PaymentsInitBinderModelEvent(modelEvent));
    }

    @Override public int getItemCount() {
        return models.size();
    }

    public void setModels(List<PaymentsInitBinderModel> models) {
        this.models = models;
        notifyDataSetChanged();
        sizeOfModels = models.size();
    }

    public void setModel(PaymentsInitBinderModel model, int position) {
        List<String> strings = Observable.from(models)
                .map(PaymentsInitBinderModel::getCardId)
                .filter(cardId -> model.getCardId().equals(cardId))
                .toList()
                .toBlocking()
                .firstOrDefault(Collections.emptyList());
        if (strings.size() == 0) {
            models.add(position, model);
        }
        notifyItemInserted(position);
        sizeOfModels = models.size();
    }
}