/*
 * Copyright (c) 2002-2016, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.enroll.business.project;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;;
import java.io.Serializable;
import java.util.Objects;

/**
 * This is the business class for the object Project
 */
public class Project implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    @NotEmpty
    private String _strName;

    @Min ( 0 )
    private int _size;

    @Min ( 0 )
    private int _currentsize;

    private int _active;

    /**
     * Returns the Id
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * @param nId The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the Name
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     * @param strName The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    public int getSize( )
    {
        return _size;
    }

    public void setSize( int size )
    {
        _size = size;
    }

    public int getActive( )
    {
        return _active;
    }

    public void setActive( int act )
    {
        _active = act;
    }

    public void flipActive( )
    {
        if (_active == 0) {
          _active = 1;
        } else {
          _active = 0;
        }
    }

    public int getCurrentSize( )
    {
        return _currentsize;
    }

    public void setCurrentSize( int cs ) {
      _currentsize = cs;
    }

    // three convenience methods to make code easier to read
    public boolean hasRoom() {
        return (_size==0 || _currentsize < _size);
    }

    public boolean canAdd() {
        return (_active==1 && hasRoom());
    }

    public boolean atCapacity() { return _currentsize == _size && _size != 0; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return _nId == project._nId &&
                _size == project._size &&
                _currentsize == project._currentsize &&
                _active == project._active &&
                Objects.equals(_strName, project._strName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_nId, _strName, _size, _currentsize, _active);
    }


}
