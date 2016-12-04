package com.sidm.mgp_lab02_153492y;

import java.util.Vector;

/**
 * Created by Donovan's PC on 4/12/2016.
 */

public class Vector3 {

    float x, y, z;

    float EPSILON =	0.00001f;  ///Used for error checking

    Boolean IsEqual(float a, float b)
    {
        return a - b <= EPSILON && b - a <= EPSILON;
    }

    public Vector3(float a, float b, float c) {
        this.x = a;
        this.y = b;
        this.z = c;
    }

    public Vector3(Vector3 rhs) {
        this.x = rhs.x;
        this.y = rhs.y;
        this.z = rhs.z;
    }

    public void Set( float a, float b, float c) //Set all data
    {
        this.x = a;
        this.y = b;
        this.z = c;
    }

    public void SetZero() //Set all data to zero
    {
        this.x = this.y = this.z = 0.0f;
    }

    public Boolean IsZero() //Check if data is zero
    {
        return IsEqual(x, 0.f) && IsEqual(y, 0.f) && IsEqual(z, 0.f);
    }

    /*
    Vector3 operator+( const Vector3& rhs ) const; //Vector addition
    Vector3& operator+=( const Vector3& rhs );

    Vector3 operator-( const Vector3& rhs ) const; //Vector subtraction
    Vector3& operator-=( const Vector3& rhs );

    Vector3 operator-( void ) const; //Unary negation

    Vector3 operator*( float scalar ) const; //Scalar multiplication
    Vector3& operator*=( float scalar );

    bool operator==( const Vector3& rhs ) const; //Equality check
    bool operator!= ( const Vector3& rhs ) const; //Inequality check

    Vector3& operator=(const Vector3& rhs); //Assignment operator
    */

    public float Length() //Get magnitude
    {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public float LengthSquared () //Get square of magnitude
    {
        return x * x + y * y + z * z;
    }

    public float Dot(Vector3 rhs) //Dot product
    {
        return x * rhs.x + y * rhs.y + z * rhs.z;
    }

    public Vector3 Cross(Vector3 rhs) //Cross product
    {
        Vector3 temp = new Vector3(0,0,0);
        temp.Set(y * rhs.z - z * rhs.y, z * rhs.x - x * rhs.z, x * rhs.y - y * rhs.x);

        return temp;
    }

    //Return a copy of this vector, normalized
    //Throw a divide by zero exception if normalizing a zero vector
    public Vector3 Normalized()
    {
        float d = Length();
        if(d <= EPSILON && -d <= EPSILON)
        {}
        else
        {
            Vector3 temp = new Vector3(0,0,0);
            temp.Set(x / d, y / d, z / d);

            return temp;
        }
        return null;
    }

    //friend std::ostream& operator<<( std::ostream& os, Vector3& rhs); //print to ostream

    //friend Vector3 operator*( float scalar, const Vector3& rhs ); //what is this for?
}
