using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace PresentationControlServer
{
    class Program
    {
        static void Main(string[] args)
        {
            Server server = new Server();
            server.StartServer();
            server.Listen();
        }
    }
}
