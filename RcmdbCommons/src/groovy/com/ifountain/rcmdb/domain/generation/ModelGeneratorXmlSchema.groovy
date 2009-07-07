package com.ifountain.rcmdb.domain.generation
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 7, 2009
 * Time: 10:59:39 AM
 * To change this template use File | Settings | File Templates.
 */
class ModelGeneratorXmlSchema {
    public final static String MODEL_XML_XSD = """
    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">
        <xs:complexType name="Model">
            <xs:attribute name="name" type="xs:string"/>
            <xs:attribute name="parentModel" type="xs:string"/>
            <xs:attribute name="indexName" type="xs:string"/>
            <xs:attribute name="storageType" type="xs:string"/>
            <xs:attribute name="blank" type="xs:boolean"/>
            <xs:attribute name="nullable" type="xs:boolean"/>
        </xs:complexType>
        <xs:complexType name="Property">
            <xs:attribute name="name" type="xs:string"/>
            <xs:attribute name="parentModel" type="xs:string"/>
            <xs:attribute name="indexName" type="xs:string"/>
            <xs:attribute name="storageType" type="xs:string"/>
        </xs:complexType>
        <xs:complexType name="Key">
            <xs:attribute name="propertyName" type="xs:string"/>
            <xs:attribute name="nameInDatasource" type="xs:string"/>
        </xs:complexType>
        <xs:complexType name="Datasource">
            <xs:attribute name="name" type="xs:string"/>
            <xs:attribute name="mappedName" type="xs:string"/>
            <xs:attribute name="mappedNameProperty" type="xs:string"/>
        </xs:complexType>

        <xs:complexType name="Relation">
            <xs:attribute name="name" type="xs:string"/>
            <xs:attribute name="cardinality" type="xs:string"/>
            <xs:attribute name="reverseCardinality" type="xs:string"/>
            <xs:attribute name="reverseName" type="xs:string"/>
            <xs:attribute name="toModel" type="xs:string"/>
            <xs:attribute name="isOwner" type="xs:boolean"/>
        </xs:complexType>

        <xs:element name="Model" type="Model">
            <xs:complexType>
                <xs:element name="Properties"  minOccurs="0">
                    <xs:complexType>
                        <xs:element name="Property" type="Property"  maxOccurs="unbounded"/>
                    </xs:complexType>
                </xs:element>

                <xs:element name="Datasources" minOccurs="0">
                    <xs:complexType>
                        <xs:element name="Datasource" type="Datasource"  maxOccurs="unbounded">
                            <xs:complexType>
                                <xs:element name="Key" type="Key" maxOccurs="unbounded"/>
                            </xs:complexType>
                       </xs:element>
                    </xs:complexType>
                </xs:element>

                <xs:element name="Relations"  minOccurs="0">
                    <xs:complexType>
                        <xs:element name="Relation" type="Relation"  maxOccurs="unbounded"/>
                    </xs:complexType>
                </xs:element>
            </xs:complexType>
        </xs:element>

    </xs:schema>
    """
}