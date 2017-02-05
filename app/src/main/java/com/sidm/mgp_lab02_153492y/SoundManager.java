package com.sidm.mgp_lab02_153492y;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by Donovan's PC on 13/1/2017.
 */

public class SoundManager {

    private MediaPlayer BGM;

    private SoundPool Sounds;
    private AudioAttributes audioAttributes;
    private int SoundJump, SoundDie, SoundSlice, SoundRock, SoundFloat;

    public void Init()
    {
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        Sounds = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(3) // Number of SFXs
                .build();
    }

    public void LoadSounds(Context context)
    {
        BGM = MediaPlayer.create(context, R.raw.music);

        SoundJump = Sounds.load(context, R.raw.jump, 1);

        SoundDie = Sounds.load(context, R.raw.defeat, 1);

        SoundSlice = Sounds.load(context, R.raw.slice, 1);

        SoundRock = Sounds.load(context, R.raw.stone, 1);

        SoundFloat = Sounds.load(context, R.raw.floating, 1);
    }

    public void PlaySound(String ID)
    {
        switch (ID)
        {
            case "Jump":
            {
                Sounds.play(SoundJump, 1.0f, 1.0f, 0, 0, 1.5f);
                break;
            }
            case "Die":
            {
                Sounds.play(SoundDie, 1.0f, 1.0f, 0, 0, 1.5f);
                break;
            }

            case "Slice":
            {
                Sounds.play(SoundSlice, 1.0f, 1.0f, 0, 0, 1.5f);
                break;
            }

            case "Rock":
            {
                Sounds.play(SoundRock, 1.0f, 1.0f, 0, 0, 1.5f);
                break;
            }

            case "Float":
            {
                Sounds.play(SoundFloat, 1.0f, 1.0f, 0, 0, 1.5f);
                break;
            }
        }
    }

    public void SetBGMVolume(float LeftBGMVol, float RightBGMVol)
    {
        BGM.setVolume(LeftBGMVol, RightBGMVol);
    }

    public void PauseBGM()
    {
        BGM.pause();
    }

    public void StartBGM()
    {
        BGM.start();
    }

    public void StopBGM()
    {
        BGM.stop();
    }

    public void Exit()
    {
        StopBGM();
        Sounds.unload(SoundJump);
        Sounds.unload(SoundDie);
        Sounds.release();
    }
}
