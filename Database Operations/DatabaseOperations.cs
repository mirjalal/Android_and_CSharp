using System;
using MySql.Data.MySqlClient;
using System.Windows.Forms;
using System.IO;
using System.Collections.Generic;
using Newtonsoft.Json.Linq;
using System.Linq;
using Newtonsoft.Json;
using System.Data;

namespace Database_Operations
{
    public partial class DatabaseOperations : Form
    {
        MySqlConnection conn = new MySqlConnection(@"server=45.35.4.29;uid=root;password=connection;database=db_webservice;");


        public DatabaseOperations()
        {
            try
            {
                conn.Open();
            }
            catch (Exception)
            {
                MessageBox.Show("Couldn't connect to server. Please, try again later.", "Information", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            }

            InitializeComponent();
        }


        private void export_Click(object sender, EventArgs e)
        {
            if (conn.State == ConnectionState.Open)
            {
                DataTable dt;

                using (MySqlDataAdapter select = new MySqlDataAdapter(@"SELECT _id, _username, _password, _name, _surname, _graduated_from, _graduated_in, _born_place, _birthday FROM users;", conn))
                {
                    using (dt = new DataTable())
                    {
                        select.Fill(dt);
                    }
                }

                string json = JsonConvert.SerializeObject(dt, Formatting.None);
                File.WriteAllText(Environment.GetFolderPath(Environment.SpecialFolder.Desktop) + @"\export.im", json);

                MessageBox.Show("Success!", "Export status", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
            else
                MessageBox.Show("Error: Operation can't be done until program connects to the server.", "Export status", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }


        private void import_Click(object sender, EventArgs e)
        {
            if (conn.State == ConnectionState.Open)
            {
                Stream myStream = null;
                OpenFileDialog import = new OpenFileDialog();

                import.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.Desktop);
                import.Filter = "im files (*.im)|*.im";
                import.FilterIndex = 1;
                import.RestoreDirectory = true;

                if (import.ShowDialog() == DialogResult.OK)
                {
                    try
                    {
                        if ((myStream = import.OpenFile()) != null)
                        {
                            string json = File.ReadAllText(import.FileName);
                            //string reconfigured_json = json.Replace('"', '\''); // why I wrote this line? shit...

                            List<int> _id = new List<int>();
                            List<string> _username = new List<string>();
                            List<string> _password = new List<string>();
                            List<string> _name = new List<string>();
                            List<string> _surname = new List<string>();
                            List<string> _graduated_from = new List<string>();
                            List<string> _graduated_in = new List<string>();
                            List<string> _born_place = new List<string>();
                            List<string> _birthday = new List<string>();

                            foreach (JObject jObject in JArray.Parse(json))
                            {
                                _id.Add((int)jObject["_id"]);
                                _username.Add((string)jObject["_username"]);
                                _password.Add((string)jObject["_password"]);
                                _name.Add((string)jObject["_name"]);
                                _surname.Add((string)jObject["_surname"]);
                                _graduated_from.Add((string)jObject["_graduted_from"]);
                                _graduated_in.Add((string)jObject["_graduted_in"]);
                                _born_place.Add((string)jObject["_born_place"]);
                                _birthday.Add((string)jObject["_birthday"]);
                            }

                            string drop_MySql_table = @"DROP TABLE IF EXISTS `users`;";
                            string create_MySql_table = @"CREATE TABLE `users` (`_id` int(11) NOT NULL AUTO_INCREMENT, `_username` varchar(45) NOT NULL, `_password` varchar(45) NOT NULL, `_name` varchar(45) DEFAULT NULL,  `_surname` varchar(45) DEFAULT NULL, `_graduated_from` varchar(45) DEFAULT NULL, `_graduated_in` varchar(45) DEFAULT NULL, `_born_place` varchar(45) DEFAULT NULL, `_birthday` varchar(45) DEFAULT NULL, PRIMARY KEY (`_id`)) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;";

                            string values = string.Empty;

                            for (int i = 0; i < _id.Count; i++)
                                values += @"(" + _id.ElementAt(i) + ", '" + _username.ElementAt(i) + "', '" + _password.ElementAt(i) + "', '" + _name.ElementAt(i) + "', '" + _surname.ElementAt(i) + "', '" + _graduated_from.ElementAt(i) + "', '" + _graduated_in.ElementAt(i) + "', '" + _born_place.ElementAt(i) + "', '" + _birthday.ElementAt(i) + "'),";

                            values = values.Remove(values.Length - 1, 1);
                            values += ";";
                            string insert_to_MySql = @"INSERT INTO `users` VALUES" + values;

                            using (MySqlCommand drop = new MySqlCommand(drop_MySql_table, conn))
                            {
                                drop.ExecuteNonQuery();
                            }

                            using (MySqlCommand create = new MySqlCommand(create_MySql_table, conn))
                            {
                                create.ExecuteNonQuery();
                            }

                            using (MySqlCommand insert = new MySqlCommand(insert_to_MySql, conn))
                            {
                                insert.ExecuteNonQuery();
                            }

                            // clean-up part
                            _id.Clear();
                            _id.TrimExcess();
                            _username.Clear();
                            _username.TrimExcess();
                            _password.Clear();
                            _password.TrimExcess();
                            _name.Clear();
                            _name.TrimExcess();
                            _surname.Clear();
                            _surname.TrimExcess();
                            _graduated_from.Clear();
                            _graduated_from.TrimExcess();
                            _graduated_in.Clear();
                            _graduated_in.TrimExcess();
                            _born_place.Clear();
                            _born_place.TrimExcess();
                            _birthday.Clear();
                            _birthday.TrimExcess();

                            MessageBox.Show("Success!", "Import status", MessageBoxButtons.OK, MessageBoxIcon.Information);
                        }
                        else
                            MessageBox.Show("You've not picked file yet.", "Import status", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    }
                    catch (Exception)
                    {
                        MessageBox.Show("Error: Could not read file from disk.", "Import status", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    }
                    finally
                    {
                        myStream.Close();
                    }
                }
            }
            else
                MessageBox.Show("Error: Operation can't be done until program connects to the server.", "Import status", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }
}
