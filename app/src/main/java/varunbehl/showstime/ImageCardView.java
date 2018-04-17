package varunbehl.showstime;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import varunbehl.showstime.databinding.ImageCardBinding;

public class ImageCardView extends ConstraintLayout {
    private ImageCardBinding binding;

    public ImageCardView(Context context) {
        super(context);
    }

    public ImageCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.image_card, this, true);
        setData();
    }

    private void setData() {
    }
}
