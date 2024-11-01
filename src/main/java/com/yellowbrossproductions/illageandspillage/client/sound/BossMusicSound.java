package com.yellowbrossproductions.illageandspillage.client.sound;

import com.yellowbrossproductions.illageandspillage.client.tool.ControlledAnimation;
import com.yellowbrossproductions.illageandspillage.entities.RagnoEntity;
import com.yellowbrossproductions.illageandspillage.util.IllageAndSpillageSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.raid.Raider;

public class BossMusicSound extends AbstractTickableSoundInstance {
    private Raider boss;
    private int ticksExisted = 0;
    private int timeUntilFade;
    private final SoundEvent soundEvent;
    ControlledAnimation volumeControl;

    public BossMusicSound(SoundEvent sound, Raider boss) {
        super(sound, SoundSource.RECORDS, boss.getRandom());
        this.boss = boss;
        this.soundEvent = sound;
        this.attenuation = Attenuation.NONE;
        this.looping = true;
        this.delay = 0;
        this.x = boss.getX();
        this.y = boss.getY();
        this.z = boss.getZ();
        this.volumeControl = new ControlledAnimation(40);
        this.volumeControl.setTimer(20);
        this.volume = this.volumeControl.getAnimationFraction();
        this.timeUntilFade = 80;
        this.looping = false;
    }

    public boolean canPlaySound() {
        return BossMusicPlayer.bossMusic == this;
    }

    public void tick() {
        if (this.boss != null && this.boss.isAlive() && !this.boss.isSilent()) {
            this.volumeControl.increaseTimer();
            this.timeUntilFade = 60;
        } else {
            if (this.boss != null && !this.boss.isAlive()) {
                this.timeUntilFade = 0;
            }

            this.boss = null;
            if (this.timeUntilFade > 0) {
                --this.timeUntilFade;
            } else {
                this.volumeControl.decreaseTimer();
            }
        }

        if (boss instanceof RagnoEntity && soundEvent == IllageAndSpillageSoundEvents.ENTITY_RAGNO_TRANS.get() && this.ticksExisted >= 2831) {
            this.stop();
            BossMusicPlayer.bossMusic = null;
            BossMusicSound newMusic = new BossMusicSound(((RagnoEntity) boss).getBossMusic(), boss);
            Minecraft.getInstance().getSoundManager().play(newMusic);
            BossMusicPlayer.bossMusic = newMusic;
            return;
        }

        if ((double) this.volumeControl.getAnimationFraction() < 0.025) {
            this.stop();
            BossMusicPlayer.bossMusic = null;
        }

        this.volume = this.volumeControl.getAnimationFraction() * 0.4f;
        if (this.ticksExisted % 100 == 0) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
        }

        ++this.ticksExisted;
    }

    public void setBoss(Raider boss) {
        this.boss = boss;
    }

    public Raider getBoss() {
        return this.boss;
    }

    public SoundEvent getSoundEvent() {
        return this.soundEvent;
    }
}