package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.network.MeatgunNetwork;

public class RecoilManager
{
    public MeatgunNetwork.RecoilDirection direction;
    public float amount;
    public float horAmount;
    public float returnSpeed;
    public float horReturnSpeed;

    public void set(MeatgunNetwork.RecoilDirection direction, float amount, float horAmount, float returnSpeed, float horReturnSpeed)
    {
        this.direction = direction;
        this.amount = amount;
        this.horAmount = horAmount;
        this.returnSpeed = returnSpeed;
        this.horReturnSpeed = horReturnSpeed;
    }
}
