package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.network.MeatgunS2C;

public class RecoilManager
{
    public MeatgunS2C.RecoilDirection direction;
    public float amount;
    public float horAmount;
    public float returnSpeed;
    public float horReturnSpeed;

    public void set(MeatgunS2C.RecoilDirection direction, float amount, float horAmount, float returnSpeed, float horReturnSpeed)
    {
        this.direction = direction;
        this.amount = amount;
        this.horAmount = horAmount;
        this.returnSpeed = returnSpeed;
        this.horReturnSpeed = horReturnSpeed;
    }
}
