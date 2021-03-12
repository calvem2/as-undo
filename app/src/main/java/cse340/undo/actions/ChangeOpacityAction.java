package cse340.undo.actions;

import android.support.annotation.NonNull;

import cse340.undo.app.DrawingView;

/**
 * Reversible action which changes the opacity of the DrawingView's paint.
 */
public class ChangeOpacityAction extends AbstractReversibleAction {
        /** The opacity that this action changes the current paint to. */
        private final int mOpacity;

        /** The opacity that this action changes the current paint from. */
        private int mPrev;

        /**
         * Creates an action that changes the paint opacity.
         *
         * @param opacity New opacity for DrawingView paint.
         */
        public ChangeOpacityAction(int opacity) { this.mOpacity = opacity; }

        /** @inheritDoc */
        @Override
        public void doAction(DrawingView view) {
            super.doAction(view);
            mPrev = view.getCurrentPaint().getAlpha();
            view.getCurrentPaint().setAlpha(mOpacity);

        }

        /** @inheritDoc */
        @Override
        public void undoAction(DrawingView view) {
            super.undoAction(view);
            view.getCurrentPaint().setAlpha(mPrev);
        }

        /** @inheritDoc */
        @NonNull
        @Override
        public String toString() {
            return "Change opacity to " + mOpacity;
        }

}
