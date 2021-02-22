using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Configuration;

namespace ConsoleApp1
{
    class DB
    {
        private static readonly string CONNECTION_STRING_NAME="Connection";
        private static readonly string SHOP_TABLE = "Shop";
        private static readonly string ERROR_MESSAGE = "Error occured: {0}";

        private static readonly List<string> TableNames=new ();
        private static SqlConnection _connection;
        private static DataSet _dataSet;
        private static SqlDataAdapter _dataAdapter;
        private static Mode _mode=Mode.Connected;
        private static bool _initialized;

        public DB(params string[] tables)
        {
            foreach (var name in tables)
            {
                TableNames.Add(name);
            }
        }
        
        public void Connect()
        {
            string connectionString = ConfigurationManager.ConnectionStrings[CONNECTION_STRING_NAME].ConnectionString;
            _connection = new SqlConnection(connectionString);
            try
            {
                _connection.Open();
            }
            catch (Exception e)
            {
                Console.Write((ERROR_MESSAGE, e));
                Environment.Exit(-1);
            }
        }

        public void Disconnect()
        {
            try
            {
                _connection.Close();
                Console.WriteLine("Connection closed");
            }
            catch (Exception e)
            {
                Console.WriteLine((ERROR_MESSAGE,e));
                Environment.Exit(-1);
            }
        }

        public void ShowTableContents(string tableName)
        {
            if (_mode == Mode.Connected)
            {
                ShowTableContentsConnected(tableName);
            }
            else
            {
                ShowTableContentsDisconnected(tableName);
            }
        }

        public void DeleteFromShop(string orientField,string parameter)
        {
            if (_mode == Mode.Connected)
            {
                DeleteFromShopConnected(orientField,parameter);
            }
            else
            {
                DeleteFromShopDisconnected(orientField,parameter);
            }
        }

        public void InsertIntoShop(string name,string address,int score)
        {
            if (_mode == Mode.Connected)
            {
                InsertIntoShopConnected(name,address,score);
            }
            else
            {
                InsertIntoShopDisconnected(name,address,score);
            }
        }

        public void UpdateShopValues(string newName,string newAddress,int newScore,int id)
        {
            if (_mode == Mode.Connected)
            {
                UpdateShopConnected(newName,newAddress,newScore,id);
            }
            else
            {
                UpdateShopDisconnected(newName,newAddress,newScore,id);
            }
        }

        public void SetMode(Mode mode)
        {
            _mode = mode;
            if (_mode == Mode.Disconnected && !_initialized)
            {
                ConfigureDisconnectedLayer();
                _initialized = true;
            }
        }
        
        public enum Mode
        {
            Connected,Disconnected
        }

        private void UpdateShopConnected(string newName,string newAddress,int newScore,int id)
        {
            Connect();
            SqlCommand command = _connection.CreateCommand();
            command.Connection = _connection;
            command.CommandText = $"UPDATE Shop SET name={newName}, address={newAddress}, score={newScore} WHERE id={id}";

            command.ExecuteNonQuery();
            Disconnect();
        }

        public void UpdateShopDisconnectedLayer()
        {
            if (_mode == Mode.Disconnected)
            {
                _dataAdapter.Update(_dataSet, SHOP_TABLE);
            }
        } 

        private void UpdateShopDisconnected(string newName,string newAddress,int newScore,int id)
        {
            string updateQuery = $"UPDATE Shop SET name={newName},address={newAddress},score={newScore} WHERE id={id}";
            _dataAdapter.UpdateCommand = new SqlCommand(updateQuery, _connection);

            int rowsNumber = _dataSet.Tables[SHOP_TABLE].Rows.Count;

            for (int i = 0; i < rowsNumber; i++)
            {
                DataRow row = _dataSet.Tables[SHOP_TABLE].Rows[i];
                if (row["id"]!=DBNull.Value && (int)row["id"] == id)
                {
                    row["name"] = newName;
                    row["address"] = newAddress;
                    row["score"] = newScore;
                }
            }
        }

        private void InsertIntoShopConnected(string name,string address,int score)
        {
            Connect();
            try
            {
                SqlCommand command = _connection.CreateCommand();
                command.Connection = _connection;
                command.CommandText = $"INSERT INTO Shop VALUES ({name},{address},{score})";

                command.ExecuteNonQuery();
            }
            catch (Exception e)
            {
                Console.WriteLine((ERROR_MESSAGE,e));
            }
            Disconnect();
        }

        private void InsertIntoShopDisconnected(string name,string address,int score)
        {
            string insertQuery = $"INSERT INTO Shop VALUES ({name},{address},{score})";

            _dataAdapter.InsertCommand = new SqlCommand(insertQuery,_connection);

            DataRow row = _dataSet.Tables[SHOP_TABLE].NewRow();
            row["name"] = name;
            row["address"] = address;
            row["score"] = score;
            
            _dataSet.Tables[SHOP_TABLE].Rows.Add(row);
        }

        private void DeleteFromShopConnected(string orientField,string parameter)
        {
            Connect();
            try
            {
                SqlCommand command = _connection.CreateCommand();
                command.Connection = _connection;
                command.CommandText = $"DELETE FROM Shop WHERE {orientField}={parameter}";

                command.ExecuteNonQuery();
            }
            catch (Exception e)
            {
                Console.WriteLine((ERROR_MESSAGE,e));
            }
            Disconnect();
        }

        private void DeleteFromShopDisconnected(string orientField,string parameter)
        {
            string deleteQuery = $"DELETE FROM Shop WHERE {orientField}={parameter}";
            _dataAdapter.DeleteCommand = new SqlCommand(deleteQuery,_connection);

            int rowsNumber = _dataSet.Tables[SHOP_TABLE].Rows.Count;

            for (int i = 0; i < rowsNumber; i++)
            {
                DataRow row= _dataSet.Tables[SHOP_TABLE].Rows[i];
                if (row[orientField].ToString() == parameter)
                {
                    row.Delete();
                }
            }
        }

        private void ShowTableContentsConnected(string tableName)
        {
            Connect();
            try
            {
                SqlCommand command = _connection.CreateCommand();
                command.Connection = _connection;
                command.CommandText = $"SELECT * FROM {tableName}";

                SqlDataReader reader = command.ExecuteReader();

                for (int i = 0; i < reader.FieldCount; i++)
                {
                    Console.Write($"{reader.GetName(i)}\t");
                }
                Console.WriteLine();
                while (reader.Read())
                {
                    for (int i = 0; i < reader.FieldCount; i++)
                    {
                        Console.Write($"{reader.GetValue(i)}\t");
                    }
                    Console.WriteLine();
                }
                Console.WriteLine();
            
                reader.Close();
            }
            catch (Exception e)
            {
                Console.WriteLine((ERROR_MESSAGE,e));
            }
            Disconnect();
        }
        
        private void ShowTableContentsDisconnected(string tableName)
        {
            SqlDataAdapter dataAdapter = new SqlDataAdapter($"SELECT * FROM {tableName}", _connection);
            DataSet dataSet = new DataSet();
            dataAdapter.Fill(dataSet);

            DataTableReader tableReader = dataSet.CreateDataReader();
            for (int i = 0; i < tableReader.FieldCount; i++)
            {
                Console.Write($"{tableReader.GetName(i)}\t");
            }
            Console.WriteLine();
            while (tableReader.Read())
            {
                for (int i = 0; i < tableReader.FieldCount; i++)
                {
                    Console.Write($"{tableReader.GetValue(i)}\t");
                }
                Console.WriteLine();
            }
            Console.WriteLine();
            
            tableReader.Close();
        }

        private void ConfigureDisconnectedLayer()
        {
            _dataSet = new DataSet();
            foreach (var name in TableNames)
            {
                _dataAdapter = new SqlDataAdapter($"SELECT * FROM {name}", _connection);
                _dataAdapter.Fill(_dataSet,name);
            }
        }
    }
    
    class Program
    {
        private static readonly DB Db = new ("Shop", "Customer");
        
        private static void TestConnectedLayer()
        {
            Db.SetMode(DB.Mode.Connected);

            Db.ShowTableContents("Shop");
            
            Db.UpdateShopValues("'try'","'ss'",6,1);
            Db.ShowTableContents("Shop");
            
            Db.DeleteFromShop("id","1");
            Db.ShowTableContents("Shop");
            
            Db.InsertIntoShop("'Shop3'","'Address3'",7);
            Db.ShowTableContents("Shop");
        }

        private static void TestDisconnectedLayer()
        {
            Db.Connect();
            Db.SetMode(DB.Mode.Disconnected);
            
            Db.ShowTableContents("Shop");

            Db.InsertIntoShop("'Shop3'","'Address3'",7);

            Db.UpdateShopValues("'next try'","'sss'",9,1);
            
            Db.DeleteFromShop("id","2");

            Db.UpdateShopDisconnectedLayer();
            
            Db.ShowTableContents("Shop");
            
            Db.Disconnect();
        }
        
        static void Main()
        {
            //TestConnectedLayer();
            TestDisconnectedLayer();
        }
    }
}