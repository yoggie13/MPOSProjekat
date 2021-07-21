using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Threading.Tasks;
using WindowsInput;

namespace PresentationControlServer
{
    internal enum Slide
    {
        STOP = 0,
        Previous = 1,
        Next = 2
    }
    class Server
    {
        private Socket serverSocket;
        private Socket clientSocket;
        
        public bool StartServer()
        {
            try
            {
                serverSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                string address = GetLocalIPAddress();
                if (address != "")
                {
                    IPEndPoint endPoint = new IPEndPoint(IPAddress.Parse(address), 10000);
                    serverSocket.Bind(endPoint);
                    serverSocket.Listen(1);
                    Console.WriteLine("Vaša IP adresa je: " + address);
                    return true;
                }
                else throw new Exception();
            }
            catch (Exception)
            {
                return false;
            }
        }
        
        public void Listen()
        {
            try
            {
                Console.WriteLine("Čeka se konekcija");
                clientSocket = serverSocket.Accept();
                Console.WriteLine($"Povezan klijent na {clientSocket.RemoteEndPoint}");
               
                this.AcceptRequest();

            }
            catch (Exception)
            {

            }
            
        }
        public void AcceptRequest()
        {
            try
            {
                NetworkStream stream = new NetworkStream(clientSocket);
                BinaryFormatter formatter = new BinaryFormatter();
                StreamReader streamReader = new StreamReader(stream);
                Slide s = (Slide)Enum.GetValues(typeof(Slide)).GetValue(Convert.ToInt32(streamReader.ReadLine()));
                
                Console.WriteLine(s);

                if(s == Slide.STOP)
                    throw new Exception();

                ChangeSlide(s);
                
                this.AcceptRequest();
            }
            catch (Exception)
            {
                this.disconnect();
            }

        }
        private void disconnect()
        {
            clientSocket.Shutdown(SocketShutdown.Both);
            clientSocket.Disconnect(true);
            Console.WriteLine("Klijent se diskonektovao");
            this.Listen();
        }
       
        private void ChangeSlide(Slide s)
        {
            InputSimulator i = new InputSimulator();

            if (s == Slide.Previous)
            {
                i.Keyboard.KeyPress(WindowsInput.Native.VirtualKeyCode.LEFT);
            }
            else
            {
                i.Keyboard.KeyPress(WindowsInput.Native.VirtualKeyCode.RIGHT);
            }
        }
        public static string GetLocalIPAddress()
        {
            var host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (var ip in host.AddressList)
            {
                if (ip.AddressFamily == AddressFamily.InterNetwork)
                {
                    return ip.ToString();
                }
            }
            return "";
        }

    }
}
