package com.ifountain.domain

class ModelGenerator
{
    private static ModelGenerator generator;
    private ModelGenerator()
    {

    }

    public static ModelGenerator getInstance()
    {
        if(!generator)
        {
            generator = new ModelGenerator();
        }
        return generator;
    }

    def generateModel(model)
    {
        
    }
}