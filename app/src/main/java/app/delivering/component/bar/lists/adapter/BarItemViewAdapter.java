package app.delivering.component.bar.lists.adapter;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yayandroid.parallaxrecyclerview.ParallaxImageView;
import com.yayandroid.parallaxrecyclerview.ParallaxViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import app.R;
import app.core.bars.list.get.entity.BarListModel;
import app.delivering.mvp.animation.ShowViewSubBinder;
import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;
import app.delivering.mvp.bars.list.init.enums.BarListFilterType;
import app.delivering.mvp.bars.list.init.model.SortedBarListModel;
import app.delivering.mvp.bars.list.item.click.events.OnBarItemClickEvent;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import ru.rambler.libs.swipe_layout.SwipeLayout;

public class BarItemViewAdapter extends RecyclerView.Adapter<BarItemViewAdapter.ViewHolder> {
    private static final String APPROVED_STATUS = "approved";
    private SortedBarListModel listModel;
    private Point displaySize;
    private BarListFilterType filterType;
    private int[] itemsOffset;
    private OnBarItemClickEvent clickEvent;
    private boolean isClickedOnSwipedContainer;

    private static final int CLICK_DISTANCE = 15;
    private static final int SWIPE_FULL_DISTANCE = 100;
    private static final int SWIPE_MAX_LEFT_SIDE_DISTANCE = 200;
    private static final int SWIPE_DISTANCE_FOR_TRIGGER = 30;
    private static final double MINIMAL_DISTANCE_IN_KM = 0.25;
    private static final int PARAM_ZERO = 0;
    private static final int [] ANIM_DURATIONS = { 200, 400, 500, 600, 800 };
    private float pressedX;
    private float pressedY;

    public class ViewHolder extends ParallaxViewHolder {
        @BindView(R.id.list_item_bar_photo) ParallaxImageView container;
        @BindView(R.id.list_item_bar_broken_photo) ImageView brokenContainer;
        @BindView(R.id.list_item_bar_type_indicator) ImageView barTypeIndicator;
        @BindView(R.id.bar_list_image_progress) MaterialProgressBar imageProgress;
        @BindView(R.id.venue_special_promotion) Button specialPromotion;
        @BindView(R.id.list_item_bar_type) TextView barType;
        @BindView(R.id.list_item_bar_name) TextView barName;
        @BindView(R.id.list_item_bar_route) TextView barRouting;
        @BindView(R.id.list_item_bar_discount) TextView barDiscount;
        @BindView(R.id.list_item_bar_closed_text) TextView barClosedText;
        @BindView(R.id.list_item_bar_opens_text) TextView barOpensText;
        @BindView(R.id.bar_item_swiped_text) TextView swipedText;
        @BindView(R.id.list_item_fb_friends) TextView fbFriends;
        @BindView(R.id.bar_item_swipe) SwipeLayout swipeLayout;
        @BindView(R.id.list_item_bar_swipe_container) RelativeLayout swipeContainer;
        @BindView(R.id.list_item_bar_container) FrameLayout cardContainer;
        @BindView(R.id.animated_arrows_root) LinearLayout animatedArrowsRoot;
        @BindView(R.id.first_animated_arrow) ImageView firstAnimatedArrow;
        @BindView(R.id.second_animated_arrow) ImageView secondAnimatedArrow;
        @BindView(R.id.third_animated_arrow) ImageView thirdAnimatedArrow;

        @Override
        public int getParallaxImageId() {
            return R.id.list_item_bar_photo;
        }

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public BarItemViewAdapter(BarListFilterType filterType) {
        this.filterType = filterType;
        clickEvent = new OnBarItemClickEvent(filterType);
        listModel = new SortedBarListModel(new ArrayList<>(), false, false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bar_with_swipe,
                parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        String text = holder.swipedText.getText().toString();
        if (!text.isEmpty())
            activateArrowsAnim(holder);
        else
            disableArrowsAnim(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        disableArrowsAnim(holder);
        if (holder.getAdapterPosition() != RecyclerView.NO_POSITION && itemsOffset.length != 0) {
            itemsOffset[holder.getAdapterPosition()] = holder.swipeLayout.getOffset();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (listModel.getVenueList() == null || listModel.getVenueList().size() <= position) return;
        BarListModel barModel = listModel.getVenueList().get(position);
        resetState(holder);
        setSwipe(holder, 0, position);

        if (barModel.getBarWorkTimeType() != BarByWorkTime.CLOSED) {
            if (barModel.getCheckInId() != null)
                setSwipe(holder, R.string.view_tab, position);
            else if (!listModel.isOpenedTabExisting() && barModel.getDistKm() < MINIMAL_DISTANCE_IN_KM)
                setSwipe(holder, R.string.open_tab, position);
        }
        show(holder, barModel);
        holder.swipeLayout.setOffset(itemsOffset[position]);
    }

    private void activateArrowsAnim(ViewHolder holder) {
        if (!listModel.haveArrowsBeenShowed()) {
            holder.animatedArrowsRoot.setVisibility(View.VISIBLE);
            addAnimation(holder.firstAnimatedArrow, ANIM_DURATIONS[4], PARAM_ZERO);
            addAnimation(holder.secondAnimatedArrow, ANIM_DURATIONS[1], ANIM_DURATIONS[1]);
            addAnimation(holder.thirdAnimatedArrow, ANIM_DURATIONS[0], ANIM_DURATIONS[3]);
        }
    }

    private void addAnimation(View animatedArrow, int duration, int startOffset) {
        Animation animSetUp = new AlphaAnimation(0.0f, 1.0f);
        animSetUp.setDuration(duration);
        animSetUp.setStartOffset(startOffset);
        animSetUp.setRepeatMode(Animation.REVERSE);
        animSetUp.setRepeatCount(Animation.INFINITE);
        animatedArrow.startAnimation(animSetUp);
    }

    private void disableArrowsAnim(ViewHolder holder) {
        holder.firstAnimatedArrow.clearAnimation();
        holder.secondAnimatedArrow.clearAnimation();
        holder.thirdAnimatedArrow.clearAnimation();
        holder.animatedArrowsRoot.setVisibility(View.GONE);
    }

    private void setSwipe(ViewHolder holder, int textId, int position) {
        int startTranslationSize;
        if (textId != 0) {
            startTranslationSize = -holder.container.getContext().getResources().getDimensionPixelSize(R.dimen.dip40);
            updateLayoutParams(holder.swipeLayout, 0);
            holder.cardContainer.setPadding(holder.swipeLayout.getContext().getResources().getDimensionPixelOffset(R.dimen.dip33), 0, 0, 0);
            resetParameters(holder, startTranslationSize);
            activateArrowsAnim(holder);
            setUpOnSwipeContainerClickOptions(holder, position);
        } else {
            startTranslationSize = 0;
            disableArrowsAnim(holder);
        }
        activateSwipe(holder, startTranslationSize, textId, position);
    }

    private void resetState(ViewHolder holder) {
        holder.swipeLayout.animateReset();
        holder.cardContainer.setPadding(0, 0, 0, 0);
        holder.cardContainer.setTranslationX(0);
        holder.swipeContainer.setTranslationX(0);
        holder.swipeLayout.setRightSwipeEnabled(false);
        updateLayoutParams(holder.swipeLayout, holder.swipeLayout.getContext().getResources().getDimensionPixelOffset(R.dimen.dip10));
        clickEvent.setShouldOpenBySwiping(false);
    }

    private void resetParameters(ViewHolder holder, int startTranslationSize) {
        holder.swipeLayout.animateReset();
        resizeSwipeContainer(holder, startTranslationSize);
        holder.swipeContainer.setPadding(0,0,0,0);
    }

    private void updateLayoutParams(SwipeLayout swipeLayout, int startEndMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) swipeLayout.getLayoutParams();
        layoutParams.setMargins(startEndMargin, 0, startEndMargin,
                swipeLayout.getContext().getResources().getDimensionPixelOffset(R.dimen.dip16));
        swipeLayout.setLayoutParams(layoutParams);
    }

    private void setUpOnSwipeContainerClickOptions(ViewHolder holder, int pos) {
        holder.swipeContainer.setOnClickListener(v -> {
            isClickedOnSwipedContainer = true;
            clickEvent.setShouldOpenBySwiping(true);
            clickEvent.setSwipingBeforeThreshold(false);
            clickEvent.setSwipingText(holder.swipedText.getText().toString());
            postOnClickEvent(holder, pos);
        });
    }

    private void setUpOnItemClickOptions(ViewHolder holder, MotionEvent event, int pos) {
        if (!isClickedOnSwipedContainer) {
            if (countDistance(holder.swipeContainer.getContext(), pressedX, pressedY, event.getX(), event.getY()) < CLICK_DISTANCE) {
                clickEvent.setShouldOpenBySwiping(false);
                clickEvent.setSwipingBeforeThreshold(false);
                postOnClickEvent(holder, pos);
            } else {
                holder.swipeContainer.setPadding(0,0,0,0);
                holder.swipeLayout.animateReset();
            }
        }
    }

    private void setUpOnSwipeOptions(int swipingDistance, int threshold, MotionEvent e2,
                                     ViewHolder holder, int pos, int startTranslationSize) {
        if (swipingDistance >= threshold && e2.getX() < SWIPE_MAX_LEFT_SIDE_DISTANCE) {
            clickEvent.setShouldOpenBySwiping(true);
            clickEvent.setSwipingBeforeThreshold(false);
            clickEvent.setSwipingText(holder.swipedText.getText().toString());
            postOnClickEvent(holder, pos);
        }

        if (swipingDistance < threshold && swipingDistance > CLICK_DISTANCE) {
            clickEvent.setShouldOpenBySwiping(false);
            clickEvent.setSwipingBeforeThreshold(true);
        }

        if (checkOutsideSwiping(e2, holder)) {
            resetParameters(holder, startTranslationSize);
            clickEvent.setShouldOpenBySwiping(false);
            clickEvent.setSwipingBeforeThreshold(true);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void activateSwipe(ViewHolder holder, int startTranslationSize, int text, int position) {
        if (text != 0) {
            holder.swipeLayout.setRightSwipeEnabled(true);
            holder.swipedText.setText(text);
            holder.cardContainer.setTranslationX(startTranslationSize);
            holder.swipeContainer.setTranslationX(startTranslationSize);
        } else
            holder.swipedText.setText("");

        holder.swipeLayout.setOnTouchListener((v, event) -> {
            try{
                return onTouch(event, holder, startTranslationSize, text, position);
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        });
    }

    private boolean onTouch(MotionEvent event, ViewHolder holder, int startTranslationSize, int text, int position) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            pressedX = event.getX();
            pressedY = event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            setUpOnItemClickOptions(holder, event, position);
        }

        if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            resetParameters(holder, startTranslationSize);
            return false;
        } else {
            GestureDetector gestureDetector = setUpGestureDetector(holder.cardContainer.getContext(), holder,
                                                                   startTranslationSize, text, position);
            return gestureDetector.onTouchEvent(event);
        }
    }

    private GestureDetector setUpGestureDetector(Context context, ViewHolder holder,
                                                 int startTranslationSize, int text, int position ) {
        return new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (text != 0) {
                    setUpOnScrollLogic(holder, e2, startTranslationSize, position);
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    private void setUpOnScrollLogic(ViewHolder holder, MotionEvent event, int startTranslationSize, int pos) {
        int threshold = countSwipeThreshold(displaySize.x);
        int distance = displaySize.x - Math.abs(holder.cardContainer.getRight() + startTranslationSize);
        int swipingDistance = Math.abs((holder.swipeContainer.getRight() - holder.swipeContainer.getLeft() + startTranslationSize) / 2);

        resizeSwipeContainer(holder, distance);
        holder.swipeContainer.setPadding(swipingDistance, 0, 0, 0);
        setUpOnSwipeOptions(swipingDistance, threshold, event, holder, pos, startTranslationSize);
    }

    private float countDistance(Context context, float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(context, distanceInPx);
    }

    private static float pxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    private int countSwipeThreshold(int size) {
        return (size * SWIPE_DISTANCE_FOR_TRIGGER) / SWIPE_FULL_DISTANCE;
    }

    private boolean checkOutsideSwiping(MotionEvent m, ViewHolder holder) {
        return m.getX() < 0 || m.getY() < 0 || m.getX() > holder.swipeLayout.getWidth() || m.getY() > holder.swipeLayout.getHeight();
    }

    private void resizeSwipeContainer(ViewHolder holder, int right) {
        ViewGroup.LayoutParams params = holder.swipeContainer.getLayoutParams();
        params.width = Math.abs(right);
        holder.swipeContainer.setLayoutParams(params);
    }

    private void show(ViewHolder holder, BarListModel model) {
        showImage(holder, model);
        showMainInformation(holder, model);
        try {
            chooseWorkType(holder, model);
        } catch (Exception e){
            e.printStackTrace();
        }
        holder.getBackgroundImage().reuse();
    }

    private void showImage(ViewHolder holder, BarListModel model) {
        int weight = holder.container.getContext().getResources().getDimensionPixelOffset(R.dimen.dip380);
        int height = holder.container.getContext().getResources().getDimensionPixelOffset(R.dimen.dip360);
        Picasso picasso = Picasso.with(holder.container.getContext());
        picasso.load(model.getBackgroundUrl())
                .resize(weight, height)
                .into(holder.container, new Callback() {
                    @Override public void onSuccess() {
                        animateImage(holder.container);
                    }

                    @Override public void onError() {
                        holder.imageProgress.setVisibility(View.GONE);
                        ShowViewSubBinder.animateShow(holder.brokenContainer, SWIPE_MAX_LEFT_SIDE_DISTANCE);
                    }
                });
    }

    private void showMainInformation(ViewHolder holder, BarListModel model) {
        holder.barName.setText(model.getName());
        holder.barType.setText(model.getType().getName());
        holder.barDiscount.setText(model.getDiscountText());
        holder.barRouting.setText(model.getRouting());
        holder.barTypeIndicator.setColorFilter(Color.parseColor(model.getType().getColor()));
        holder.specialPromotion.setVisibility(
                !TextUtils.isEmpty(model.getSpecialNotice()) && TextUtils.equals(model.getSpecialNoticeStatus(), APPROVED_STATUS)
                        ? View.VISIBLE : View.GONE);
        if (model.getCheckinedFriendsNumber() > 0) {
            holder.fbFriends.setVisibility(View.VISIBLE);
            holder.fbFriends.setText(String.valueOf(model.getCheckinedFriendsNumber()));
        } else
            holder.fbFriends.setVisibility(View.GONE);
    }

    private void chooseWorkType(ViewHolder holder, BarListModel model) {
        switch (model.getBarWorkTimeType()) {
            case CLOSES_SOON:
                if (!TextUtils.isEmpty(model.getWorkTypeText())) {
                    holder.barClosedText.setVisibility(View.VISIBLE);
                    holder.barClosedText.setText(model.getWorkTypeText());
                }
                break;
            case CLOSED:
                if (!TextUtils.isEmpty(model.getWorkTypeText())) {
                    holder.barOpensText.setVisibility(View.VISIBLE);
                    holder.barOpensText.setText(model.getWorkTypeText());
                }
                break;
            default:
                holder.barClosedText.setVisibility(View.GONE);
                holder.barOpensText.setVisibility(View.GONE);
        }
    }

    private void animateImage(ImageView container) {
        if (container.isAttachedToWindow()) {
            int x = container.getWidth();
            int y = container.getHeight();
            float radius = container.getWidth();
            Animator anim = ViewAnimationUtils.createCircularReveal(container, x, y, 0, radius);
            anim.setDuration(500);
            if (container.isAttachedToWindow())
                anim.start();
        }
    }

    private void postOnClickEvent(ViewHolder holder, int position) {
        if (listModel.getVenueList() == null || listModel.getVenueList().size() <= position) return;
        BarListModel barModel = listModel.getVenueList().get(position);
        clickEvent.setBarId(barModel.getId());
        clickEvent.setDistance(barModel.getRouting());
        clickEvent.setStartedView(holder.container);
        clickEvent.setTypeIndicator(holder.barTypeIndicator);
        clickEvent.setName(holder.barName);
        clickEvent.setType(holder.barType);
        clickEvent.setDiscount(holder.barDiscount);
        clickEvent.setRoute(holder.barRouting);
        clickEvent.setImage(barModel.getBackgroundUrl());
        clickEvent.setDistanceKm(barModel.getDistKm());
        clickEvent.setBarWorkType(barModel.getBarWorkTimeType());
        disableArrowsAnim(holder);

        if (!clickEvent.getSwipingBeforeThreshold()) {
            EventBus.getDefault().postSticky(clickEvent);
            sendAnalytics(barModel);
        }
        clickEvent.setShouldOpenBySwiping(false);
        isClickedOnSwipedContainer = false;
    }

    private void sendAnalytics(BarListModel barModel) {
        MixpanelSendGateway.send(MixpanelEvents.getBarSelectedEvent(barModel.getName(),
                barModel.getDiscountText(),
                barModel.getCity(),
                barModel.getNeighborhood(),
                filterType.name(),
                MixpanelEvents.LIST))
                .subscribe(res -> {}, e->{}, ()->{});
    }

    @Override
    public int getItemCount() {
        return listModel.getVenueList().size();
    }

    public void setDisplaySize(Point displaySize) {
        this.displaySize = displaySize;
    }

    public void setModels(SortedBarListModel sortedBarListModel) {
        itemsOffset = new int[sortedBarListModel.getVenueList().size()];
        this.listModel = sortedBarListModel;
        notifyDataSetChanged();
    }
}
