package components;

import imgui.ImGui;
import imgui.type.ImString;
import util.AssetPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StateMachine extends Component {
    private class StateTrigger{
        public String state;
        public String trigger;

        public StateTrigger(){

        }

        public StateTrigger(String state, String trigger){
            this.state = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object o){
            if(o.getClass() != StateTrigger.class)return false;

            StateTrigger t2 = (StateTrigger)o;
            return t2.trigger.equals(this.trigger) && t2.state.equals(this.state);
        }

        @Override
        public int hashCode(){
            return Objects.hash(state, trigger);
        }
    }

    public HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
    private List<AnimationState> states = new ArrayList<>();
    private transient AnimationState currentState = null;
    private String defaultStateTitle = "";

    public void refreshTextures(){
        for(AnimationState state: states){
            state.refreshTexture();
        }
    }

    public void setDefaultState(String animationTitle){
        for(AnimationState state: states){
            if(state.title.equals(animationTitle)){
                defaultStateTitle = animationTitle;
                if(currentState == null){
                    currentState = state;
                }
                return;
            }
        }
        System.out.println("Unable to find default state '" + animationTitle + "'");
    }

    public void addTrigger(String from, String to, String onTrigger){
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void addState(AnimationState state){
        this.states.add(state);
    }

    public void trigger(String trigger){
        for(StateTrigger origin: stateTransfers.keySet()){
            if(origin.state.equals(currentState.title) && origin.trigger.equals(trigger)){
                 int newStateIndex = stateIndexOf(stateTransfers.get(origin));
                if (newStateIndex > -1) {
                    currentState = states.get(newStateIndex);
                }
                return;
            }
        }
        //System.out.println("Unable to find trigger '" + trigger + "'");
    }

    private int stateIndexOf(String stateTitle) {
        int index = 0;
        for (AnimationState state : states) {
            if (state.title.equals(stateTitle)) {
                return index;
            }
            index++;
        }

        return -1;
    }

    @Override
    public void init(){
        for(AnimationState state: states){
            if(state.title.equals(defaultStateTitle)){
                currentState = state;
                //currentState.refreshTexture();
                break;
            }
        }
    }

    @Override
    public void update(float dt){
        if(currentState != null){
            currentState.update(dt);
            SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
            if(spr != null){
                spr.setSprite(currentState.getCurrentSprite());
                //spr.setTexture(currentState.getCurrentSprite().getTexture());
            }
        }
    }

    @Override
    public void editorUpdate(float dt){
        if(currentState != null){
            currentState.update(dt);
            SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
            if(spr != null){
                spr.setSprite(currentState.getCurrentSprite());
                //spr.setTexture(currentState.getCurrentSprite().getTexture());
            }
        }
    }

    @Override
    public void imGui(){
        for(AnimationState state: states){
            ImString title = new ImString(state.title);
            ImGui.inputText("State: ", title);

            int index = 0;
            for(Frame frame: state.animationFrames){
                float[] tmp = new float[1];
                tmp[0] = frame.frameTime;
                ImGui.dragFloat("Frame(" + index + ") Time: ", tmp, 0.01f);
                frame.frameTime = tmp[0];
                index++;
            }
        }
    }
}