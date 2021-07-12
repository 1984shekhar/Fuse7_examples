package com.mycompany.cxf.soap.model;



import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "PersonService")
public interface PersonService {
    Person getPerson(@WebParam(name="id")int id) throws PersonException;

    Person putPerson(Person person);

    Person deletePerson(@WebParam(name="id")int id) throws PersonException;
}
