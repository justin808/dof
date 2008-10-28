package sample;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.*;
import org.hibernate.cfg.*;
import org.h2.tools.Server;

import java.sql.*;
import java.util.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({EntityTestInProcessDb.class, EntityTestInProcessDb.class, EntityTestInProcessDb.class})
public class EntityTestInProcessDb3Times
{

}